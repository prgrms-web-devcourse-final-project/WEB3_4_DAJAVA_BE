package com.dajava.backend.domain.event.es.scheduler.vaildation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.domain.event.exception.PointerEventException;
import com.dajava.backend.global.component.analyzer.MoveAnalyzerProperties;


/**
 * 무브 이벤트를 분석합니다.
 * 이상 데이터인 경우 true로 마킹합니다
 * @author NohDongHui
 */
@Component
public class EsMoveEventAnalyzer implements EsAnalyzer<PointerMoveEventDocument> {

	private final long timeWindowMs;
	private final int turnThreshold;
	private final double angleThresholdDegrees;

	public EsMoveEventAnalyzer(MoveAnalyzerProperties props) {
		this.timeWindowMs = props.getTimeWindowMs();
		this.turnThreshold = props.getTurnThreshold();
		this.angleThresholdDegrees = props.getAngleThresholdDegrees();
	}

	@Override
	public void analyze(List<PointerMoveEventDocument> eventDocuments) {
		//가져올때 es에서 정렬해 가져옴
		detectZigzagMovementByAngle(eventDocuments);

	}

	/**
	 * 짫은 시간 내 여러 방향으로 움직인지 검출합니다.
	 * 이상 데이터인 경우 true를 반환합니다.
	 * 시간 복잡도 너무 커질 경우 제외 할 수 도 있음
	 * @author NohDongHui
	 */
	public void detectZigzagMovementByAngle(List<PointerMoveEventDocument> events) {
		if (events == null || events.size() < 3) {
			return;
		}

		final int maxWindowSize = 50; // 시간 기준으로 보통 이 정도면 충분
		PointerMoveEventDocument[] window = new PointerMoveEventDocument[maxWindowSize];
		int start = 0, end = 0;

		for (PointerMoveEventDocument current : events) {
			// 윈도우에 현재 이벤트 추가
			window[end % maxWindowSize] = current;
			end++;

			// 오래된 이벤트 제거 (시간 조건)
			while (start < end && isOutOfTimeRange(window[start % maxWindowSize], current)) {
				start++;
			}

			int windowSize = end - start;
			if (windowSize >= 3) {
				List<EsVector> vectors = new ArrayList<>();

				for (int i = start + 1; i < end; i++) {
					PointerMoveEventDocument prev = window[(i - 1) % maxWindowSize];
					PointerMoveEventDocument next = window[i % maxWindowSize];
					vectors.add(new EsVector(
						next.getClientX() - prev.getClientX(),
						next.getClientY() - prev.getClientY()
					));
				}

				int turnCount = 0;
				Set<PointerMoveEventDocument> tempOutliers = new HashSet<>();

				for (int i = 1; i < vectors.size(); i++) {
					EsVector v1 = vectors.get(i - 1);
					EsVector v2 = vectors.get(i);
					double angle = v1.angleWith(v2);

					if (angle >= angleThresholdDegrees) {
						turnCount++;
						PointerMoveEventDocument e1 = window[(start + i - 1) % maxWindowSize];
						PointerMoveEventDocument e2 = window[(start + i) % maxWindowSize];
						tempOutliers.add(e1);
						tempOutliers.add(e2);
					}
				}

				if (turnCount >= turnThreshold) {
					for (PointerMoveEventDocument outlier : tempOutliers) {
						try {
							outlier.markAsOutlier();
						} catch (PointerEventException ignored) {}
					}
					// 윈도우 리셋 (중복 감지 방지)
					start = end;
				}
			}
		}
	}
	private boolean isOutOfTimeRange(PointerMoveEventDocument first, PointerMoveEventDocument current) {
		return Duration.between(first.getTimestamp(), current.getTimestamp()).toMillis() > timeWindowMs;
	}

	// 내부 벡터 클래스 (2D)
	public static class EsVector {
		final int dx;
		final int dy;

		public EsVector(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		public double angleWith(EsVector other) {
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