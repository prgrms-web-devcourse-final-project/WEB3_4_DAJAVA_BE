package com.dajava.backend.domain.event.scheduler.vaildation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.PointerScrollEvent;
import com.dajava.backend.domain.event.SessionData;

/**
 * 스크롤 이벤트를 분석합니다.
 * 이상 데이터인 경우 true 를 반환합니다.
 * @author NohDongHui
 */
@Component
public class ScrollEventAnalyzer implements Analyzer {

	private static final long TIME_WINDOW_MS = 3000;
	private static final int MIN_SCROLL_DELTA = 300; // 변경됨
	private static final int MIN_EVENT_COUNT = 3;
	private static final int RAGE_THRESHOLD_PER_WINDOW = 3; // 윈도우 안에서 3번 이상 rage scroll

	private static final int MIN_DIRECTION_CHANGES = 3; //방향 전환 횟수

	private static final int SCROLL_BOTTOM_THRESHOLD = 2000; // 컨텐츠 소모 정도 감지하는 기준

	@Override
	@Transactional
	public boolean analyze(SessionData sessionData) {
		List<PointerScrollEvent> events = sessionData.getPointerScrollEvents();

		return detectBackAndForthScroll(events)
			|| detectTopRepeatScroll(events)
			|| countRageScrollBursts(events);
		// 해당하는 경우, sessionData의 isVerified를 true로 변경
	}

	/**
	 * rage scroll을 감지합니다.
	 * 짫은 시간 내 여러번 rage scroll이 있는 경우
	 * @param events PointerClickEvent 리스트
	 * @return 비정상적인 클릭 이벤트가 있을 경우 true 반환 아닌경우 false
	 */
	public boolean countRageScrollBursts(List<PointerScrollEvent> events) {
		if (events == null || events.size() < MIN_EVENT_COUNT) {
			return false;
		}

		//db에서 오름차순 정렬해 가져옴

		LinkedList<PointerScrollEvent> window = new LinkedList<>();
		int rageBurstCount = 0;
		List<PointerScrollEvent> detectedOutliers = new ArrayList<>();

		for (PointerScrollEvent current : events) {
			window.addLast(current);

			// 오래된 이벤트 제거 (3초 넘은 것)
			while (!window.isEmpty() && isOutOfTimeRange(window.getFirst(), current)) {
				window.removeFirst();
			}

			List<PointerScrollEvent> windowOutliers = new ArrayList<>();

			// 윈도우 안에서 Rage Scroll이 3번 이상 발생했는지 검사
			int rageWithinWindow = countRageScrolls(window, windowOutliers);
			if (rageWithinWindow >= RAGE_THRESHOLD_PER_WINDOW) {
				rageBurstCount++;
				detectedOutliers.addAll(windowOutliers); // 전체 결과에 추가
				window.clear(); // 윈도우 초기화
			}
		}

		if (!detectedOutliers.isEmpty()) {
			convertEventsIsOutlier(detectedOutliers);
		}

		return rageBurstCount > 0;
	}

	/**
	 * rage scroll을 감지합니다.
	 * 짫은 시간 내 여러번 rage scroll이 있는 경우
	 * @param window PointerScrollEvent 리스트
	 * @return TIME_WINDOW_MS 동안 rage scroll 횟수 반환
	 */
	private int countRageScrolls(List<PointerScrollEvent> window, List<PointerScrollEvent> outliers) {
		int count = 0;
		int i = 0;

		while (i < window.size()) {
			List<PointerScrollEvent> subList = new ArrayList<>();
			PointerScrollEvent base = window.get(i);
			subList.add(base);

			boolean matched = false;

			for (int j = i + 1; j < window.size(); j++) {
				PointerScrollEvent next = window.get(j);

				subList.add(next);

				if (subList.size() >= MIN_EVENT_COUNT && scrollDelta(subList) >= MIN_SCROLL_DELTA) {
					count++;
					outliers.addAll(subList);
					i = j; // 여기서 i 점프! 다음 루프는 j부터 시작
					matched = true;
					break;
				}
			}

			if (!matched) {
				i++; // 조건 안 맞았으면 그냥 다음으로
			}
		}

		return count;
	}

	private boolean isOutOfTimeRange(PointerScrollEvent first, PointerScrollEvent current) {
		return Duration.between(first.getCreateDate(), current.getCreateDate()).toMillis() > TIME_WINDOW_MS;
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
	public boolean detectBackAndForthScroll(List<PointerScrollEvent> events) {
		if (events == null || events.size() < 2) {
			return false;
		}

		int directionChanges = 0;
		Integer prevY = null;
		Integer prevDirection = null; // 1: down, -1: up

		List<PointerScrollEvent> outlierCandidates = new ArrayList<>();

		for (PointerScrollEvent event : events) {
			int currentY = event.getScrollY();

			if (prevY != null) {
				int delta = currentY - prevY;
				int direction = Integer.compare(delta, 0); // 1: down, -1: up, 0: no move

				if (direction != 0 && prevDirection != null && direction != prevDirection) {
					directionChanges++;
					outlierCandidates.add(event);
				}

				if (direction != 0) {
					prevDirection = direction;
				}
			}

			prevY = currentY;
		}

		if (directionChanges >= MIN_DIRECTION_CHANGES) {
			convertEventsIsOutlier(outlierCandidates);
			return true;
		}

		return false;
	}

	/**
	 * 컨텐츠 소모율 감지
	 * 모든 스크롤 Y값이 특정 threshold 이하일 경우 true 반환
	 * 프론트에서 scrollHeight 받을 수 있음 값이 아닌 비율로 측정하게 바꿀수 있음
	 * @param events 스크롤 이벤트 목록 (시간순 정렬 가정)
	 * @return 상단 반복 스크롤 감지 시 true
	 */
	public boolean detectTopRepeatScroll(List<PointerScrollEvent> events) {

		if (events == null || events.isEmpty()) {
			return false;
		}

		boolean allUnderThreshold = events.stream()
			.allMatch(e -> e.getScrollY() <= SCROLL_BOTTOM_THRESHOLD);

		if (allUnderThreshold) {
			// 모든 이벤트가 임계값 이하일 때 → 가장 큰 scrollY 가진 이벤트 추출
			PointerScrollEvent maxScrollYEvent = events.stream()
				.max(Comparator.comparingInt(PointerScrollEvent::getScrollY))
				.orElse(null);

			convertEventsIsOutlier(List.of(maxScrollYEvent));

			return true;
		}

		return false;

	}

	void convertEventsIsOutlier(List<PointerScrollEvent> events) {
		for (PointerScrollEvent e : events) {
			e.setOutlier();
		}
	}
}
