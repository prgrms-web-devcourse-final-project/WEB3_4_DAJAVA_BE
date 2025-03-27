package com.dajava.backend.domain.event.scheduler.vaildation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.PointerClickEvent;
import com.dajava.backend.domain.event.SessionData;

import lombok.extern.slf4j.Slf4j;

/**
 * 클릭 이벤트를 분석합니다.
 * 이상 데이터인 경우 true를 반환합니다.
 * @author NohDongHui
 */
@Slf4j
@Component
public class ClickEventAnalyzer implements Analyzer {

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
	 * @return boolean true 인 경우 해당 세션 데이터의 클릭 이벤트는 이상치임
	 */
	@Override
	public boolean analyze(SessionData sessionData) {
		boolean analyzeResult1 = detectRageClicks(sessionData.getPointerClickEvents());
		boolean analyzeResult2 = detectSuspiciousClicks(sessionData.getPointerClickEvents());

		if (analyzeResult1 || analyzeResult2) {
			return true;
		}
		return false;
	}

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

		// 1. 시간순 정렬 db 에서 정렬해 가져옴

		// rage Click 이 일어난 로그 이벤트 리스트
		// 2번 발생했다면 rageGroups 리스트 크기는 2
		List<List<PointerClickEvent>> rageGroups = new ArrayList<>();
		// rage Click 인지 검사하는 윈도우 리스트
		LinkedList<PointerClickEvent> window = new LinkedList<>();

		for (PointerClickEvent current : clickEvents) {
			window.addLast(current);

			// 윈도우 조건: createDate 5초 이내, 위치 ±10px
			while (!window.isEmpty() && isOutOfTimeRange(window.getFirst(), current)) {
				window.removeFirst();
			}

			// current 기준 ±10px 넘는 경우 inRange 리스트에서 삭제
			List<PointerClickEvent> inRange = window.stream()
				.filter(e -> isInProximity(e, current))
				.toList();

			// 필터링 후 남은 데이터 사이즈가 MIN_CLICK_COUNT를 넘기면 rage click으로 간주
			if (inRange.size() >= MIN_CLICK_COUNT) {
				rageGroups.add(new ArrayList<>(inRange));
				window.clear(); // 감지 후 윈도우 초기화 (중복 방지)
			}
		}

		// rage 그룹에 존재하는 모든 클릭 이벤트의 이상치 플래그 변경
		rageGroups.stream()
			.flatMap(List::stream)
			.forEach(PointerClickEvent::setOutlier);

		log.info("클릭 이벤트 로깅: {}", rageGroups);

		// rage 그룹이 존재하면 true 반환 아니면 false
		return !rageGroups.isEmpty();
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
	 * @return 비정상적인 클릭 이벤트가 있을 경우 true 반환 아닌경우 false
	 */
	public boolean detectSuspiciousClicks(List<PointerClickEvent> events) {
		if (events == null || events.isEmpty()) {
			return false;
		}

		List<PointerClickEvent> suspiciousClicks = events.stream()
			.filter(this::isSuspiciousClick)
			.toList();

		return !suspiciousClicks.isEmpty();
	}

	private boolean isSuspiciousClick(PointerClickEvent event) {
		String tag = "div"; //event.getClickTag(); 현재 이벤트 객체에 태그 필드 없음
		if (tag == null || tag.isBlank())
			return false;

		String lowerTag = tag.toLowerCase();

		// 클릭한 태그가 의심 태그거나
		boolean tagMatch = SUSPICIOUS_TAGS.stream().anyMatch(lowerTag::startsWith);

		// class 속성에 의미 없는 단어가 포함되어 있거나
		boolean classMatch = SUSPICIOUS_CLASS_KEYWORDS.stream().anyMatch(lowerTag::contains);

		// onclick 속성 없음 (추가로 hasOnClick() 같은 게 있으면 활용 가능)
		boolean hasOnClick = lowerTag.contains("onclick");

		// 클릭 이벤트가 이상치 조건을 만족하면 이상치 플래그 변경 및 true 반환
		if ((tagMatch || classMatch) && !hasOnClick) {
			event.setOutlier();
			return true;
		}
		return false;
	}
}

