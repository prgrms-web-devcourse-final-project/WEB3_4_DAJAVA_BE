package com.dajava.backend.domain.event.scheduler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.dajava.backend.domain.event.PointerClickEvent;

public class ClickEventAnalyzer {

	//5초 내 클릭 한지 감지
	private static final int TIME_THRESHOLD_MS = 5000;
	private static final int POSITION_THRESHOLD_PX = 10;
	private static final int MIN_CLICK_COUNT = 3;

	/**
	 * Rage Click으로 의심되는 클릭 그룹들을 탐지합니다.
	 *
	 * @param clickEvents 클릭 이벤트 리스트 (동일 사용자/세션 기준)
	 * @return Rage Click으로 판단된 클릭 그룹 리스트
	 */
	public boolean detectRageClicks(List<PointerClickEvent> clickEvents) {
		if (clickEvents == null || clickEvents.size() < MIN_CLICK_COUNT) {
			return false;
		}

		// 1. 시간순 정렬 db 에서 정렬하서 가저옴

		// rage click이 일어난 로그 이벤트 리스트 2번 일어났음 rageGroups 리스트 크기는 2
		List<List<PointerClickEvent>> rageGroups = new ArrayList<>();
		// rageClick인지 검사하는 윈도우 리스트
		LinkedList<PointerClickEvent> window = new LinkedList<>();

		for (PointerClickEvent current : clickEvents) {
			window.addLast(current);

			// 윈도우 조건: createdAt 5초 이내, 위치 ±10px
			while (!window.isEmpty() && isOutOfTimeRange(window.getFirst(), current)) {
				window.removeFirst();
			}

			List<PointerClickEvent> inRange = window.stream()
				.filter(e -> isInProximity(e, current))
				.collect(Collectors.toList());

			if (inRange.size() >= MIN_CLICK_COUNT) {
				rageGroups.add(new ArrayList<>(inRange));
				window.clear(); // 감지 후 윈도우 초기화 (중복 방지)
			}
		}
		//rage 그룹이 존재하면 true 반환 아니면 false
		return !rageGroups.isEmpty();
	}

	private boolean isOutOfTimeRange(PointerClickEvent first, PointerClickEvent current) {
		return Duration.between(first.getCreateDate(), current.getCreateDate()).toMillis() > TIME_THRESHOLD_MS;
	}

	private boolean isInProximity(PointerClickEvent clickEvent1, PointerClickEvent clickEvent2) {
		return (Math.abs(clickEvent1.getClientX() - clickEvent2.getClientX()) <= POSITION_THRESHOLD_PX &&
			Math.abs(clickEvent1.getClientY() - clickEvent2.getClientY()) <= POSITION_THRESHOLD_PX);
	}
}
