package com.dajava.backend.domain.event.scheduler.vaildation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.PointerClickEvent;
import com.dajava.backend.domain.event.PointerMoveEvent;
import com.dajava.backend.domain.event.SessionData;

import lombok.extern.slf4j.Slf4j;

/**
 * 클릭 이벤트를 분석합니다.
 * 이상 데이터인 경우 true를 반환합니다.
 * @author NohDongHui
 */
@Slf4j
@Component
public class ClickEventAnalyzer implements Analyzer<PointerClickEvent> {

	//5초 내 클릭 한지 감지
	private static final int TIME_THRESHOLD_MS = 5000;
	private static final int POSITION_THRESHOLD_PX = 10;
	private static final int MIN_CLICK_COUNT = 3;

	// 비정상 클릭으로 판단되는 태그 목록
	private static final Set<String> SUSPICIOUS_TAGS = Set.of(
		"div", "span", "p", "li", "img", "label", "section", "article", "body"
	);

	// class 이름 일부로 감지되는 의미 없는 영역
	private static final List<String> SUSPICIOUS_CLASS_KEYWORDS = List.of(
		"container", "wrapper", "background"
	);

	/**
	 * 클릭 이벤트를 검증해 이상치가 존재하는지 판단하는 구현체
	 * @param sessionData 세션 데이터의 클릭 이벤트를 가져오기 위함
	 * @return 이상치 데이터를  중복없이 반환함
	 */
	@Override
	public List<PointerClickEvent> analyze(SessionData sessionData) {
		List<PointerClickEvent> rageClicks = getRageClicks(sessionData.getPointerClickEvents());
		List<PointerClickEvent> suspiciousClicks = getSuspiciousClicks(sessionData.getPointerClickEvents());

		// 중복 제거를 위해 Set 사용
		Set<PointerClickEvent> resultSet = new HashSet<>();
		resultSet.addAll(rageClicks);
		resultSet.addAll(suspiciousClicks);

		return new ArrayList<>(resultSet);
	}

	/**
	 * Rage Click으로 의심되는 클릭 그룹들을 탐지합니다.
	 *
	 * @param clickEvents 클릭 이벤트 리스트 (동일 사용자/세션 기준)
	 * @return Rage Click으로 판단된 클릭 그룹 리스트
	 */
	public List<PointerClickEvent> getRageClicks(List<PointerClickEvent> clickEvents) {
		if (clickEvents == null || clickEvents.size() < MIN_CLICK_COUNT) {
			return Collections.emptyList();
		}

		// 1. 시간순 정렬 db 에서 정렬해 가져옴

		Set<PointerClickEvent> rageClicks = new HashSet<>();
		LinkedList<PointerClickEvent> window = new LinkedList<>();

		for (PointerClickEvent current : clickEvents) {
			window.addLast(current);

			while (!window.isEmpty() && isOutOfTimeRange(window.getFirst(), current)) {
				window.removeFirst();
			}

			List<PointerClickEvent> inRange = window.stream()
				.filter(e -> isInProximity(e, current))
				.toList();

			if (inRange.size() >= MIN_CLICK_COUNT) {
				rageClicks.addAll(inRange);
				window.clear(); // 중복 감지 방지
			}
		}


		log.info("감지된 rage click 이벤트 수: {}", rageClicks.size());

		return new ArrayList<>(rageClicks);
	}

	private boolean isOutOfTimeRange(PointerClickEvent first, PointerClickEvent current) {
		return Duration.between(first.getCreateDate(), current.getCreateDate()).toMillis() > TIME_THRESHOLD_MS;
	}

	private boolean isInProximity(PointerClickEvent clickEvent1, PointerClickEvent clickEvent2) {
		return (Math.abs(clickEvent1.getClientX() - clickEvent2.getClientX()) <= POSITION_THRESHOLD_PX)
			&& (Math.abs(clickEvent1.getClientY() - clickEvent2.getClientY()) <= POSITION_THRESHOLD_PX);
	}

	/**
	 * 비정상적인 클릭 이벤트를 필터링합니다.
	 *
	 * @param events PointerClickEvent 리스트
	 * @return 이상치 데이터 리스트 중복 없이 반환
	 */
	public List<PointerClickEvent> getSuspiciousClicks(List<PointerClickEvent> events) {
		if (events == null || events.isEmpty()) {
			return Collections.emptyList();
		}

		return events.stream()
			.filter(this::isSuspiciousClick)
			.collect(Collectors.toList());
	}

	private boolean isSuspiciousClick(PointerClickEvent event) {
		// TODO: event.getClickTag()로 실제 태그 받아오도록 변경 예정
		String tag = "div"; // 현재는 하드코딩되어 있음

		if (tag == null || tag.isBlank()) {
			return false;
		}

		String lowerTag = tag.toLowerCase();

		// 클릭한 태그가 의심 태그거나
		boolean tagMatch = SUSPICIOUS_TAGS.stream().anyMatch(lowerTag::startsWith);

		// class 속성에 의미 없는 단어가 포함되어 있거나
		boolean classMatch = SUSPICIOUS_CLASS_KEYWORDS.stream().anyMatch(lowerTag::contains);

		// onclick 속성 없음 (임시 방식, 향후 별도 필드로 분리 권장)
		boolean hasOnClick = lowerTag.contains("onclick");

		// 클릭 이벤트가 이상치 조건을 만족하면 플래그 설정 및 true 반환
		if ((tagMatch || classMatch) && !hasOnClick) {
			return true;
		}

		return false;
	}
}

