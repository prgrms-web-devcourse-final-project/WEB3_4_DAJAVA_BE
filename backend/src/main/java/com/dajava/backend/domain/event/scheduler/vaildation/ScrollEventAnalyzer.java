package com.dajava.backend.domain.event.scheduler.vaildation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.dajava.backend.domain.event.PointerScrollEvent;

public class ScrollEventAnalyzer {

	private static final long TIME_WINDOW_MS = 3000;
	private static final int MIN_SCROLL_DELTA = 300; // 변경됨
	private static final int MIN_EVENT_COUNT = 3;
	private static final int RAGE_THRESHOLD_PER_WINDOW = 3; // 윈도우 안에서 3번 이상 rage scroll

	/**
	 * rage scroll을 감지합니다.
	 * 짫은 시간 내 여러번 rage scroll이 있는 경우
	 * @param events PointerClickEvent 리스트
	 * @return 비정상적인 클릭 이벤트가 있을 경우 true 반환 아닌경우 false
	 */
	public int countRageScrollBursts(List<PointerScrollEvent> events) {
		if (events == null || events.size() < MIN_EVENT_COUNT) return 0;

		//db에서 오름차순 정렬해 가져옴

		LinkedList<PointerScrollEvent> window = new LinkedList<>();
		int rageBurstCount = 0;

		for (PointerScrollEvent current : events) {
			window.addLast(current);

			// 오래된 이벤트 제거 (3초 넘은 것)
			while (!window.isEmpty() && isOutOfTimeRange(window.getFirst(), current)) {
				window.removeFirst();
			}

			// 윈도우 안에서 Rage Scroll이 3번 이상 발생했는지 검사
			int rageWithinWindow = countRageScrolls(window);
			if (rageWithinWindow >= RAGE_THRESHOLD_PER_WINDOW) {
				rageBurstCount++;
				window.clear(); // 윈도우 초기화 후 다음 탐색
			}
		}

		return rageBurstCount;
	}

	private int countRageScrolls(List<PointerScrollEvent> window) {
		int count = 0;
		int i = 0;

		while (i < window.size()) {
			List<PointerScrollEvent> subList = new ArrayList<>();
			PointerScrollEvent base = window.get(i);
			subList.add(base);

			boolean matched = false;

			for (int j = i + 1; j < window.size(); j++) {
				PointerScrollEvent next = window.get(j);
				long timeDiff = Duration.between(base.getCreateDate(), next.getCreateDate()).toMillis();

				subList.add(next);

				if (subList.size() >= MIN_EVENT_COUNT && scrollDelta(subList) >= MIN_SCROLL_DELTA) {
					count++;
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
}
