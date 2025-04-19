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

import lombok.extern.slf4j.Slf4j;

/**
 * 무브 이벤트를 분석합니다.
 * 이상 데이터인 경우 true로 마킹합니다
 * @author NohDongHui
 */
@Slf4j
@Component
public class EsMoveEventAnalyzer implements EsAnalyzer<PointerMoveEventDocument> {

	private final long timeWindowMs;
	private final int turnThreshold;
	private final double angleThresholdDegrees;

	private static final int MAXWINDOWSIZE = 50;


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
	 * @author NohDongHui
	 */
	public void detectZigzagMovementByAngle(List<PointerMoveEventDocument> events) {
		if (events == null || events.size() < 3) {
			return;
		}


		PointerMoveEventDocument[] window = new PointerMoveEventDocument[MAXWINDOWSIZE];
		int start = 0;
		int end = 0;

		for (PointerMoveEventDocument current : events) {
			// 윈도우에 현재 이벤트 추가
			window[end % MAXWINDOWSIZE] = current;
			end++;

			// 오래된 이벤트 제거 (시간 조건)
			start = removeOldEvents(window, start, end, current);

			int windowSize = end - start;
			if (windowSize >= 3) {
				processWindow(window, start, end);
			}
		}
	}

	/**
	 * 시간 범위를 벗어난 이벤트를 제거하고 새로운 시작 인덱스를 반환합니다.
	 */
	private int removeOldEvents(PointerMoveEventDocument[] window, int start, int end,
		PointerMoveEventDocument current) {
		while (start < end && isOutOfTimeRange(window[start % MAXWINDOWSIZE], current)) {
			start++;
		}
		return start;
	}

	/**
	 * 현재 윈도우의 이벤트를 처리하여 지그재그 움직임을 감지합니다.
	 */
	private void processWindow(PointerMoveEventDocument[] window, int start, int end) {
		List<EsVector> vectors = calculateVectors(window, start, end);

		if (vectors.isEmpty()) {
			return;
		}

		Set<PointerMoveEventDocument> outliers = detectOutliers(vectors, window, start);

		int turnCount = outliers.size() / 2; // 각 전환점은 두 이벤트와 관련됨

		if (turnCount >= turnThreshold) {
			markOutliers(outliers);
			// 윈도우 리셋은 호출자가 처리하도록 함
		}
	}

	/**
	 * 윈도우 내 이벤트들로부터 벡터를 계산합니다.
	 */
	private List<EsVector> calculateVectors(PointerMoveEventDocument[] window, int start, int end) {
		List<EsVector> vectors = new ArrayList<>();

		for (int i = start + 1; i < end; i++) {
			PointerMoveEventDocument prev = window[(i - 1) % MAXWINDOWSIZE];
			PointerMoveEventDocument next = window[i % MAXWINDOWSIZE];
			vectors.add(new EsVector(
				next.getClientX() - prev.getClientX(),
				next.getClientY() - prev.getClientY()
			));
		}

		return vectors;
	}

	/**
	 * 벡터들을 분석하여 이상 패턴을 보이는 이벤트들을 감지합니다.
	 */
	private Set<PointerMoveEventDocument> detectOutliers(List<EsVector> vectors,
		PointerMoveEventDocument[] window,
		int start) {
		Set<PointerMoveEventDocument> outliers = new HashSet<>();

		for (int i = 1; i < vectors.size(); i++) {
			EsVector v1 = vectors.get(i - 1);
			EsVector v2 = vectors.get(i);
			double angle = v1.angleWith(v2);

			if (angle >= angleThresholdDegrees) {
				PointerMoveEventDocument e1 = window[(start + i - 1) % MAXWINDOWSIZE];
				PointerMoveEventDocument e2 = window[(start + i) % MAXWINDOWSIZE];
				outliers.add(e1);
				outliers.add(e2);
			}
		}

		return outliers;
	}

	/**
	 * 감지된 이상치 이벤트들을 마킹합니다.
	 */
	private void markOutliers(Set<PointerMoveEventDocument> outliers) {
		for (PointerMoveEventDocument outlier : outliers) {
			try {
				outlier.markAsOutlier();
			} catch (PointerEventException ignored) {
				log.debug("이미 이상치로 처리된 데이터입니다: {}", outlier.getId());
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
			double thisDx = this.dx;
			double thisDy = this.dy;
			double otherDx = other.dx;
			double otherDy = other.dy;

			double dot = thisDx * otherDx + thisDy * otherDy;
			double mag1 = Math.sqrt(thisDx * thisDx + thisDy * thisDy);
			double mag2 = Math.sqrt(otherDx * otherDx + otherDy * otherDy);

			if (mag1 == 0 || mag2 == 0) {
				return 0; // 움직임이 없으면 각도 없음
			}

			double cosTheta = dot / (mag1 * mag2);
			cosTheta = Math.clamp(cosTheta, -1.0, 1.0); // 안전하게 clamp 부동 소수점 계산 문제

			return Math.toDegrees(Math.acos(cosTheta)); // 코사인 역함수로 각도 계산,라디안 → 도(degree)
		}
	}

}
