package com.dajava.backend.domain.event.scheduler.vaildation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.entity.PointerMoveEvent;
import com.dajava.backend.domain.event.entity.SessionData;
import com.dajava.backend.global.component.analyzer.MoveAnalyzerProperties;
import com.dajava.backend.global.utils.EventsUtils;

/**
 * 무브 이벤트를 분석합니다.
 * 이상 데이터인 경우 true를 반환합니다.
 * @author NohDongHui
 */
@Component
public class MoveEventAnalyzer implements Analyzer<PointerMoveEvent> {

	private final long timeWindowMs;
	private final int turnThreshold;
	private final double angleThresholdDegrees;

	public MoveEventAnalyzer(MoveAnalyzerProperties props) {
		this.timeWindowMs = props.getTimeWindowMs();
		this.turnThreshold = props.getTurnThreshold();
		this.angleThresholdDegrees = props.getAngleThresholdDegrees();
	}

	@Override
	public List<PointerMoveEvent> analyze(SessionData sessionData) {
		List<PointerMoveEvent> events = sessionData.getPointerMoveEvents();
		EventsUtils.sortByCreateDateAsc(events);
		List<PointerMoveEvent> zigzags = detectZigzagMovementByAngle(events);
		Set<PointerMoveEvent> resultSet = new HashSet<>();
		resultSet.addAll(zigzags);
		return new ArrayList<>(resultSet);
		// 해당하는 경우, sessionData의 isVerified를 true로 변경
	}

	/**
	 * 짫은 시간 내 여러 방향으로 움직인지 검출합니다.
	 * 이상 데이터인 경우 true를 반환합니다.
	 * 시간 복잡도 너무 커질 경우 제외 할 수 도 있음
	 * @author NohDongHui
	 */
	public List<PointerMoveEvent> detectZigzagMovementByAngle(List<PointerMoveEvent> events) {
		if (events == null || events.size() < 3) {
			return Collections.emptyList();
		}

		// db에서 시간 오름차순 정렬해 가져옴

		LinkedList<PointerMoveEvent> window = new LinkedList<>();
		Set<PointerMoveEvent> outliers = new HashSet<>();

		for (PointerMoveEvent current : events) {
			window.addLast(current);

			// 현재 포인터 기준으로 TIME_WINDOW_MS 초과하는 오래된 이벤트 제거
			while (!window.isEmpty() && isOutOfTimeRange(window.getFirst(), current)) {
				window.removeFirst();
			}

			// 윈도우 내 최소 3개의 이벤트 있어야 방향 계산 가능
			if (window.size() >= 3) {
				// 방향 벡터 리스트 만들기
				List<Vector> vectors = new ArrayList<>();
				for (int i = 1; i < window.size(); i++) {
					int dx = window.get(i).getClientX() - window.get(i - 1).getClientX();
					int dy = window.get(i).getClientY() - window.get(i - 1).getClientY();
					vectors.add(new Vector(dx, dy));
				}

				// 방향 변화 각도 감지
				int turnCount = 0;
				Set<PointerMoveEvent> tempOutliers = new HashSet<>();
				for (int i = 1; i < vectors.size(); i++) {
					Vector v1 = vectors.get(i - 1);
					Vector v2 = vectors.get(i);
					double angle = v1.angleWith(v2);

					if (angle >= angleThresholdDegrees) {
						turnCount++;
					}

					// 각도가 큰 변곡점에 해당하는 이벤트 2개 모두 추가
					PointerMoveEvent e1 = window.get(i - 1);
					PointerMoveEvent e2 = window.get(i);
					tempOutliers.add(e1);
					tempOutliers.add(e2);
				}

				if (turnCount >= turnThreshold) {
					outliers.addAll(tempOutliers);
				}
			}
		}

		return new ArrayList<>(outliers);
	}

	private boolean isOutOfTimeRange(PointerMoveEvent first, PointerMoveEvent current) {
		return Duration.between(first.getCreateDate(), current.getCreateDate()).toMillis() > timeWindowMs;
	}

	// 내부 벡터 클래스 (2D)
	public static class Vector {
		final int dx;
		final int dy;

		public Vector(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		public double angleWith(Vector other) {
			double dot = this.dx * other.dx + this.dy * other.dy;
			double mag1 = Math.sqrt(this.dx * this.dx + this.dy * this.dy);
			double mag2 = Math.sqrt(other.dx * other.dx + other.dy * other.dy);

			if (mag1 == 0 || mag2 == 0) {
				return 0; // 움직임이 없으면 각도 없음
			}

			double cosTheta = dot / (mag1 * mag2);
			cosTheta = Math.max(-1, Math.min(1, cosTheta)); // 안전하게 clamp 부동 소수점 계산 문제

			return Math.toDegrees(Math.acos(cosTheta)); // 코사인 역함수로 각도 계산,라디안 → 도(degree)
		}
	}

}

