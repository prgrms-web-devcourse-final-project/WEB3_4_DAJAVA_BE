package com.dajava.backend.domain.event.es.scheduler.vaildation;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.exception.PointerEventException;
import com.dajava.backend.global.component.analyzer.ClickAnalyzerProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * es 클릭 이벤트를 분석합니다.
 * 이상 데이터인 경우 outlier 값을 true로 바꿉니다.
 * @author NohDongHui
 */
@Slf4j
@Component
public class EsClickEventAnalyzer implements EsAnalyzer<PointerClickEventDocument> {

	//5초 내 클릭 한지 감지
	private final int timeThresholdMs;
	private final int positionThresholdPx;
	private final int minClickCount;

	public EsClickEventAnalyzer(ClickAnalyzerProperties props) {
		this.timeThresholdMs = props.getTimeThresholdMs();
		this.positionThresholdPx = props.getPositionThresholdPx();
		this.minClickCount = props.getMinClickCount();
	}


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
	 * @param eventDocuments 클릭 이벤트 리스트
	 * @return 내부에서 객체 isOutlier값을 변경함 void 반환
	 */
	@Override
	public void analyze(List<PointerClickEventDocument> eventDocuments) {
		//es에서 시계열로 정렬해 가져옴
		findRageClicks(eventDocuments);
		findSuspiciousClicks(eventDocuments);

	}

	/**
	 * Rage Click으로 의심되는 클릭 그룹들을 탐지합니다.
	 * 이상치인 경우 isOutlier 값을 변경합니다.
	 * @param clickEvents 클릭 이벤트 리스트 (동일 사용자/세션 기준)
	 * @return void
	 */
	public void findRageClicks(List<PointerClickEventDocument> clickEvents) {

		if (clickEvents == null || clickEvents.size() < minClickCount) {
			return;
		}

		PointerClickEventDocument[] window = new PointerClickEventDocument[clickEvents.size()];
		int start = 0, end = 0;

		for (PointerClickEventDocument current : clickEvents) {
			window[end++] = current;

			while (start < end && isOutOfTimeRange(window[start], current)) {
				start++;
			}

			int count = 0;
			for (int i = start; i < end; i++) {
				if (isInProximity(window[i], current)) {
					count++;
				}
			}

			if (count >= minClickCount) {
				for (int i = start; i < end; i++) {
					if (isInProximity(window[i], current)) {
						try {
							window[i].markAsOutlier();
						} catch (PointerEventException ignored) {
							// 이미 outlier인 경우 무시
						}
					}
				}
				start = end; // 중복 방지
			}
		}

		log.info("rage click 감지 완료");
	}

	private boolean isOutOfTimeRange(PointerClickEventDocument first, PointerClickEventDocument current) {
		Duration duration = Duration.between(first.getTimestamp(), current.getTimestamp());
		return duration.toMillis() > timeThresholdMs;
	}

	private boolean isInProximity(PointerClickEventDocument clickEvent1, PointerClickEventDocument clickEvent2) {
		return (Math.abs(clickEvent1.getClientX() - clickEvent2.getClientX()) <= positionThresholdPx)
			&& (Math.abs(clickEvent1.getClientY() - clickEvent2.getClientY()) <= positionThresholdPx);
	}

	/**
	 * 비정상적인 클릭 이벤트를 이상치값으로 체크합니다.
	 *
	 * @param events PointerClickEventDocument 리스트
	 * @return void
	 */
	public void findSuspiciousClicks(List<PointerClickEventDocument> events) {
		if (events == null || events.isEmpty()) {
			return;
		}

		for (PointerClickEventDocument event : events) {
			if (isSuspiciousClick(event)) {
				try {
					event.markAsOutlier();
				} catch (PointerEventException ignored) {
					// 이미 이상치로 마킹된 경우는 무시
				}
			}
		}
	}

	private boolean isSuspiciousClick(PointerClickEventDocument event) {

		String tag = event.getElement(); // 현재는 하드코딩되어 있음

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
