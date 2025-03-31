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

import com.dajava.backend.domain.event.entity.PointerScrollEvent;
import com.dajava.backend.domain.event.entity.SessionData;
import com.dajava.backend.global.component.analyzer.ScrollAnalyzerProperties;
import com.dajava.backend.global.utils.EventsUtils;

/**
 * 스크롤 이벤트를 분석합니다.
 * 이상 데이터인 경우 true 를 반환합니다.
 * @author NohDongHui
 */
@Component
public class ScrollEventAnalyzer implements Analyzer<PointerScrollEvent> {

	private final long timeWindowMs;
	private final int minScrollDelta;
	private final int minEventCount;
	private final int rageThresholdPerWindow;
	private final int minDirectionChanges;
	private final int scrollBottomThreshold;

	public ScrollEventAnalyzer(ScrollAnalyzerProperties props) {
		this.timeWindowMs = props.getTimeWindowMs();
		this.minScrollDelta = props.getMinScrollDelta();
		this.minEventCount = props.getMinEventCount();
		this.rageThresholdPerWindow = props.getRageThresholdPerWindow();
		this.minDirectionChanges = props.getMinDirectionChanges();
		this.scrollBottomThreshold = props.getScrollBottomThreshold();
	}

	@Override
	public List<PointerScrollEvent> analyze(SessionData sessionData) {
		List<PointerScrollEvent> events = sessionData.getPointerScrollEvents();
		EventsUtils.sortByCreateDateAsc(events);

		List<PointerScrollEvent> rageScrolls = countRageScrollBursts(events);
		List<PointerScrollEvent> backAndForthScrolls = getBackAndForthScrollOutliers(events);
		List<PointerScrollEvent> topRepeatScroll = getTopRepeatScrollOutliers(events);

		Set<PointerScrollEvent> resultSet = new HashSet<>();

		resultSet.addAll(rageScrolls);
		resultSet.addAll(backAndForthScrolls);
		resultSet.addAll(topRepeatScroll);

		return new ArrayList<>(resultSet);
	}

	/**
	 * rage scroll을 감지합니다.
	 * 짫은 시간 내 여러번 rage scroll이 있는 경우
	 * @param events PointerClickEvent 리스트
	 * @return rage scroll 에 해당하는 이벤트 반환
	 */
	public List<PointerScrollEvent> countRageScrollBursts(List<PointerScrollEvent> events) {
		if (events == null || events.size() < minEventCount) {
			return Collections.emptyList();
		}

		PointerScrollEvent[] window = new PointerScrollEvent[events.size()];
		int start = 0, end = 0;

		int rageBurstCount = 0;
		Set<PointerScrollEvent> detectedOutliers = new HashSet<>();

		for (PointerScrollEvent current : events) {
			window[end++] = current;

			// 오래된 이벤트 제거
			while (start < end && isOutOfTimeRange(window[start], current)) {
				start++;
			}

			// 슬라이딩 윈도우를 배열의 start~end 범위로 자르기
			List<PointerScrollEvent> windowList = new ArrayList<>(end - start);
			for (int i = start; i < end; i++) {
				windowList.add(window[i]);
			}

			List<PointerScrollEvent> windowOutliers = new ArrayList<>();
			int rageWithinWindow = countRageScrolls(windowList, windowOutliers);

			if (rageWithinWindow >= rageThresholdPerWindow) {
				rageBurstCount++;
				detectedOutliers.addAll(windowOutliers);
				start = end; // 윈도우 초기화 (중복 감지 방지)
			}
		}

		return new ArrayList<>(detectedOutliers);
	}

	/**
	 * rage scroll을 감지합니다.
	 * 짫은 시간 내 여러번 rage scroll이 있는 경우
	 * @param window PointerScrollEvent 리스트
	 * @return TIME_WINDOW_MS 동안 rage scroll 횟수 반환
	 */
	private int countRageScrolls(List<PointerScrollEvent> window, List<PointerScrollEvent> outliers) {
		int count = 0;
		int index = 0;

		while (index < window.size()) {
			List<PointerScrollEvent> subList = new ArrayList<>();
			PointerScrollEvent base = window.get(index);
			subList.add(base);

			boolean matched = false;

			for (int j = index + 1; j < window.size(); j++) {
				PointerScrollEvent next = window.get(j);

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

	private boolean isOutOfTimeRange(PointerScrollEvent first, PointerScrollEvent current) {
		return Duration.between(first.getCreateDate(), current.getCreateDate()).toMillis() > timeWindowMs;
	}

	private int scrollDelta(List<PointerScrollEvent> events) {
		int min = events.stream().mapToInt(PointerScrollEvent::getScrollY).min().orElse(0);
		int max = events.stream().mapToInt(PointerScrollEvent::getScrollY).max().orElse(0);
		return Math.abs(max - min);
	}

	/**
	 * 왕복 스크롤 여부를 감지합니다.
	 *
	 * @param events 시간순으로 정렬된 PointerScrollEvent 리스트
	 * @return 왕복 스크롤이 감지되면 true
	 */
	public List<PointerScrollEvent> getBackAndForthScrollOutliers(List<PointerScrollEvent> events) {
		if (events == null || events.size() < 2) {
			return Collections.emptyList();
		}

		int directionChanges = 0;
		Integer prevY = null;
		Integer prevDirection = null; // 1: down, -1: up

		Set<PointerScrollEvent> outliers = new HashSet<>();

		for (PointerScrollEvent event : events) {
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
			return new ArrayList<>(outliers);
		}

		return Collections.emptyList();
	}

	/**
	 * 컨텐츠 소모율 감지
	 * 모든 스크롤 Y값이 특정 threshold 이하일 경우 이상치 반환
	 * 프론트에서 scrollHeight 받을 수 있음 값이 아닌 비율로 측정하게 바꿀수 있음
	 * @param events 스크롤 이벤트 목록 (시간순 정렬 가정)
	 * @return 상단 반복 스크롤 감지 시 이상치 반환
	 */
	public List<PointerScrollEvent> getTopRepeatScrollOutliers(List<PointerScrollEvent> events) {
		if (events == null || events.isEmpty()) {
			return Collections.emptyList();
		}

		boolean allUnderThreshold = events.stream()
			.allMatch(e -> e.getScrollY() <= scrollBottomThreshold);

		if (allUnderThreshold) {
			// 이상치 판단: 상단 근처에서만 스크롤이 반복됨
			return new ArrayList<>(events);
		}

		return Collections.emptyList();
	}

}
