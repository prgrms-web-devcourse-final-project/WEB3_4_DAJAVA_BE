package com.dajava.backend.domain.event.es.scheduler.vaildation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.global.component.analyzer.ClickAnalyzerProperties;

/*
 * click docuements 를 분석하는 검증 로직 단위 테스트 입니다.
 *
 * @author NohDongHui
 * @since 2025-04-04
 */
@DisplayName("EsClickEventAnalyzer 테스트")
class EsClickEventAnalyzerTest {

	private EsClickEventAnalyzer analyzer;


	@BeforeEach
	void setUp() {
		ClickAnalyzerProperties props = new ClickAnalyzerProperties();
		props.setTimeThresholdMs(5000);
		props.setPositionThresholdPx(10);
		props.setMinClickCount(3);

		analyzer = new EsClickEventAnalyzer(props);
	}

	private PointerClickEventDocument createEvent(Long time, int clientX, int clientY, String tag) {
		return PointerClickEventDocument.builder()
			.timestamp(time)
			.clientX(clientX)
			.clientY(clientY)
			.sessionId("test-session")
			.pageUrl("/test")
			.browserWidth(1920)
			.memberSerialNumber("user-1")
			.element(tag)
			.isOutlier(false)
			.build();
	}

	private PointerMoveEventDocument createMoveEvent(Long timestamp, int clientX, int clientY) {
		return PointerMoveEventDocument.builder()
			.timestamp(timestamp)
			.clientX(clientX)
			.clientY(clientY)
			.sessionId("test-session")
			.pageUrl("/test")
			.browserWidth(1920)
			.memberSerialNumber("member-123")
			.isOutlier(false)
			.build();
	}

	@Test
	@DisplayName("Rage Click이 감지되어 isOutlier로 마킹되는지 확인")
	void testRageClickDetection() {
		LocalDateTime now = LocalDateTime.now();
		long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		List<PointerClickEventDocument> docs = List.of(
			createEvent(timestamp, 100, 100, "button"),
			createEvent(timestamp + 1, 102, 101, "button"),
			createEvent(timestamp + 2 , 103, 99, "button")
		);

		analyzer.analyze(docs);

		assertThat(docs.stream().filter(PointerClickEventDocument::getIsOutlier).count())
			.isEqualTo(3);
	}

	@Test
	@DisplayName("의심 클릭이 감지되어 isOutlier로 마킹되는지 확인")
	void testSuspiciousClickDetection() {

		LocalDateTime now = LocalDateTime.now();
		long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		PointerClickEventDocument suspicious = createEvent(timestamp, 100, 200, "div");
		List<PointerClickEventDocument> docs = List.of(suspicious);

		analyzer.analyze(docs);

		assertThat(suspicious.getIsOutlier()).isTrue();
	}

	@Test
	@DisplayName("조건에 부합하지 않으면 이상치로 마킹되지 않음")
	void testNonOutlier() {

		LocalDateTime now = LocalDateTime.now();
		long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		List<PointerClickEventDocument> docs = List.of(
			createEvent(timestamp, 100, 100, "button"),
			createEvent(timestamp + 6000, 102, 101, "button"),
			createEvent(timestamp + 12000, 103, 99, "button")
		);

		analyzer.analyze(docs);

		assertThat(docs.stream().noneMatch(PointerClickEventDocument::getIsOutlier)).isTrue();
	}
}
