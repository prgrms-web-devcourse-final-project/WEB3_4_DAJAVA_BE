package com.dajava.backend.domain.event.es.scheduler.vaildation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;
import com.dajava.backend.global.component.analyzer.ScrollAnalyzerProperties;

/*
 * scroll docuements 를 분석하는 검증 로직 단위 테스트 입니다.
 *
 * @author NohDongHui
 * @since 2025-04-04
 */
@DisplayName("EsScrollEventAnalyzer 테스트")
class EsScrollEventAnalyzerTest {

	private EsScrollEventAnalyzer analyzer;

	@BeforeEach
	void setUp() {
		ScrollAnalyzerProperties props = new ScrollAnalyzerProperties();
		props.setTimeWindowMs(3000L);
		props.setMinScrollDelta(300);
		props.setMinEventCount(3);
		props.setRageThresholdPerWindow(1); // 쉽게 검출되게 1로 설정
		props.setMinDirectionChanges(3);
		props.setContentConsumedThreshold(0.5); // 50% 이하로만 본 경우 outlier

		analyzer = new EsScrollEventAnalyzer(props);
	}

	private PointerScrollEventDocument createScrollEvent(Long time, int scrollY, int scrollHeight, int viewportHeight) {
		return PointerScrollEventDocument.builder()
			.timestamp(time)
			.scrollY(scrollY)
			.scrollHeight(scrollHeight)
			.viewportHeight(viewportHeight)
			.pageUrl("/test")
			.sessionId("session-1")
			.memberSerialNumber("member-123")
			.browserWidth(1920)
			.isOutlier(false)
			.build();
	}

	@Test
	@DisplayName("rage scroll이 감지되는 경우")
	void testRageScrollDetected() {

		LocalDateTime now = LocalDateTime.now();
		long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		List<PointerScrollEventDocument> events = List.of(
			createScrollEvent(timestamp, 100, 2000, 600),
			createScrollEvent(timestamp + 100, 500, 2000, 600),
			createScrollEvent(timestamp + 200, 900, 2000, 600)
		);

		analyzer.analyze(events);

		assertThat(events.stream().anyMatch(PointerScrollEventDocument::getIsOutlier)).isTrue();
	}

	@Test
	@DisplayName("왕복 스크롤이 감지되는 경우")
	void testBackAndForthScrollDetected() {

		LocalDateTime now = LocalDateTime.now();
		long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		List<PointerScrollEventDocument> events = List.of(
			createScrollEvent(timestamp, 100, 2000, 600),
			createScrollEvent(timestamp + 100, 150, 2000, 600),
			createScrollEvent(timestamp + 200, 100, 2000, 600),
			createScrollEvent(timestamp + 300, 160, 2000, 600),
			createScrollEvent(timestamp + 400, 110, 2000, 600)
		);

		analyzer.analyze(events);

		assertThat(events.stream().anyMatch(PointerScrollEventDocument::getIsOutlier)).isTrue();
	}

	@Test
	@DisplayName("컨텐츠 소모가 부족한 경우")
	void testTopRepeatScrollDetected() {

		LocalDateTime now = LocalDateTime.now();
		long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		List<PointerScrollEventDocument> events = List.of(
			createScrollEvent(timestamp, 100, 3000, 600),
			createScrollEvent(timestamp + 100, 300, 3000, 600),
			createScrollEvent(timestamp + 200, 500, 3000, 600)
		);

		analyzer.analyze(events);

		assertThat(events.stream().anyMatch(PointerScrollEventDocument::getIsOutlier)).isTrue();
	}

	@Test
	@DisplayName("모든 조건을 만족하지 않으면 outlier 마킹되지 않음")
	void testNoOutliersDetected() {

		LocalDateTime now = LocalDateTime.now();
		long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		List<PointerScrollEventDocument> events = List.of(
			createScrollEvent(timestamp, 100, 2000, 600),
			createScrollEvent(timestamp + 5000, 120, 2000, 600), // 시간 초과
			createScrollEvent(timestamp + 10000, 1400, 2000, 600)
		);

		analyzer.analyze(events);

		assertThat(events.stream().noneMatch(PointerScrollEventDocument::getIsOutlier)).isTrue();
	}
}

