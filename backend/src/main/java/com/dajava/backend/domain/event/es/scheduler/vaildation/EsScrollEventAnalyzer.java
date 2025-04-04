package com.dajava.backend.domain.event.es.scheduler.vaildation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.entity.PointerScrollEvent;
import com.dajava.backend.domain.event.entity.SessionData;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;
import com.dajava.backend.domain.event.exception.PointerEventException;
import com.dajava.backend.domain.event.scheduler.vaildation.Analyzer;
import com.dajava.backend.global.component.analyzer.ScrollAnalyzerProperties;
import com.dajava.backend.global.utils.EventsUtils;

/**
 * 스크롤 이벤트를 분석합니다.
 * 이상 데이터를 반환합니다.
 * @author NohDongHui
 */
@Component
public class EsScrollEventAnalyzer implements EsAnalyzer<PointerScrollEventDocument> {

	private final long timeWindowMs;
	private final int minScrollDelta;
	private final int minEventCount;
	private final int rageThresholdPerWindow;
	private final int minDirectionChanges;
	private final double contentConsumedThreshold;

	public EsScrollEventAnalyzer(ScrollAnalyzerProperties props) {
		this.timeWindowMs = props.getTimeWindowMs();
		this.minScrollDelta = props.getMinScrollDelta();
		this.minEventCount = props.getMinEventCount();
		this.rageThresholdPerWindow = props.getRageThresholdPerWindow();
		this.minDirectionChanges = props.getMinDirectionChanges();
		this.contentConsumedThreshold = props.getContentConsumedThreshold();
	}

	@Override
	public void analyze(List<PointerScrollEventDocument> documents) {
		//es 에서 조회시 정렬

		findRageScrollBursts(documents);
		findBackAndForthScrollOutliers(documents);
		findTopRepeatScrollOutliers(documents);
	}

	/**
	 * rage scroll을 감지합니다.
	 * 짫은 시간 내 여러번 rage scroll이 있는 경우
	 * rage scroll에 해당하는 데이터를 outlier로 마킹합니다.
	 * @param events PointerClickEvent 리스트
	 * @return void
	 */
	public void findRageScrollBursts(List<PointerScrollEventDocument> events) {
		if (events == null || events.size() < minEventCount) {
			return;
		}

		PointerScrollEventDocument[] window = new PointerScrollEventDocument[events.size()];
		int start = 0, end = 0;

		int rageBurstCount = 0;
		Set<PointerScrollEventDocument> detectedOutliers = new HashSet<>();

		for (PointerScrollEventDocument current : events) {
			window[end++] = current;

			// 오래된 이벤트 제거
			while (start < end && isOutOfTimeRange(window[start], current)) {
				start++;
			}

			// 슬라이딩 윈도우를 배열의 start~end 범위로 자르기
			List<PointerScrollEventDocument> windowList = new ArrayList<>(end - start);
			for (int i = start; i < end; i++) {
				windowList.add(window[i]);
			}

			List<PointerScrollEventDocument> windowOutliers = new ArrayList<>();
			int rageWithinWindow = countRageScrolls(windowList, windowOutliers);

			if (rageWithinWindow >= rageThresholdPerWindow) {
				rageBurstCount++;
				detectedOutliers.addAll(windowOutliers);
				start = end; // 윈도우 초기화 (중복 감지 방지)
			}
		}

		// 이상치로 판별된 이벤트에 markAsOutlier 호출
		for (PointerScrollEventDocument outlier : detectedOutliers) {
			try {
				outlier.markAsOutlier();
			} catch (PointerEventException e) {
				// 이미 이상치로 마킹된 경우 무시하거나 로깅 가능
				// log.warn("Already marked as outlier: {}", outlier.getId());
			}
		}
	}

	/**
	 * rage scroll을 감지합니다.
	 * 짫은 시간 내 여러번 rage scroll이 있는 경우
	 * @param window PointerScrollEvent 리스트
	 * @return TIME_WINDOW_MS 동안 rage scroll 횟수 반환
	 */
	private int countRageScrolls(List<PointerScrollEventDocument> window, List<PointerScrollEventDocument> outliers) {
		int count = 0;
		int index = 0;

		while (index < window.size()) {
			List<PointerScrollEventDocument> subList = new ArrayList<>();
			PointerScrollEventDocument base = window.get(index);
			subList.add(base);

			boolean matched = false;

			for (int j = index + 1; j < window.size(); j++) {
				PointerScrollEventDocument next = window.get(j);

				subList.add(next);

				if (subList.size() >= minEventCount && scrollDelta(subList) >= minScrollDelta) {
					count++;
					outliers.addAll(subList);
					index = j; // 여기서 i 점프 점프 하지 않는 경우 이미 rage_scroll로 판별된 이벤트 데이터를 중복 검사함
					matched = true;
					break;
				}
			}

			if (!matched) {
				index++; // 조건 안 맞았으면 그냥 다음으로
			}
		}

		return count;
	}

	private boolean isOutOfTimeRange(PointerScrollEventDocument first, PointerScrollEventDocument current) {
		return Duration.between(first.getTimestamp(), current.getTimestamp()).toMillis() > timeWindowMs;
	}

	private int scrollDelta(List<PointerScrollEventDocument> events) {
		int min = events.stream().mapToInt(PointerScrollEventDocument::getScrollY).min().orElse(0);
		int max = events.stream().mapToInt(PointerScrollEventDocument::getScrollY).max().orElse(0);
		return Math.abs(max - min);
	}

	/**
	 * 왕복 스크롤 여부를 감지합니다.
	 *
	 * @param events 시간순으로 정렬된 PointerScrollEvent 리스트
	 * @return 왕복 스크롤이 감지되면 true
	 */
	public void findBackAndForthScrollOutliers(List<PointerScrollEventDocument> events) {
		if (events == null || events.size() < 2) {
			return;
		}

		int directionChanges = 0;
		Integer prevY = null;
		Integer prevDirection = null; // 1: down, -1: up

		Set<PointerScrollEventDocument> outliers = new HashSet<>();

		for (PointerScrollEventDocument event : events) {
			int currentY = event.getScrollY();

			if (prevY != null) {
				int delta = currentY - prevY;
				int direction = Integer.compare(delta, 0); // 1: down, -1: up, 0: no move

				if (direction != 0 && prevDirection != null && direction != prevDirection) {
					directionChanges++;
					outliers.add(event);
				}

				if (direction != 0) {
					prevDirection = direction;
				}
			}

			prevY = currentY;
		}

		if (directionChanges >= minDirectionChanges) {
			for (PointerScrollEventDocument outlier : outliers) {
				try {
					outlier.markAsOutlier();
				} catch (PointerEventException e) {
					// 이미 마킹된 경우는 무시 또는 로깅 가능
					// log.warn("Already marked as outlier: {}", outlier.getId());
				}
			}
		}
	}

	/**
	 * 컨텐츠 소모율 감지해 일정 비율 이하인 경우 컨텐츠 소모를 충분히 하지 못한것으로 간주하며
	 * 간주한 데이터 중 가장 scrollY값이 큰 데이터를 outlier로 마킹합니다.
	 * @param events 스크롤 이벤트 목록 (시간순 정렬 가정)
	 * @return void
	 */
	public void findTopRepeatScrollOutliers(List<PointerScrollEventDocument> events) {
		if (events == null || events.isEmpty()) {
			return;
		}

		// 컨텐츠 소모율 계산
		double maxConsumedRatio = -1.0;
		PointerScrollEventDocument maxScrollEvent = null;

		for (PointerScrollEventDocument e : events) {
			if (e.getScrollHeight() == null || e.getScrollHeight() == 0) continue;

			int bottom = e.getScrollY() + e.getViewportHeight();
			double ratio = (double) bottom / e.getScrollHeight();

			if (ratio > maxConsumedRatio) {
				maxConsumedRatio = ratio;
				maxScrollEvent = e;
			}
		}

		// 소모율이 기준 이하라면, 가장 많이 스크롤한 이벤트만 이상치로 마킹
		if (maxConsumedRatio < contentConsumedThreshold && maxScrollEvent != null) {
			try {
				maxScrollEvent.markAsOutlier();
			} catch (PointerEventException ex) {
				// 이미 마킹된 경우는 무시하거나 로깅 가능
				// log.warn("Already marked as outlier: {}", maxScrollEvent.getId());
			}
		}
	}

}