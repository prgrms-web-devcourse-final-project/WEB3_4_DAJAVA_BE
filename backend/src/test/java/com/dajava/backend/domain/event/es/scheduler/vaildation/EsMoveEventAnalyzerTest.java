package com.dajava.backend.domain.event.es.scheduler.vaildation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.global.component.analyzer.MoveAnalyzerProperties;

/*
 * move docuements 를 분석하는 검증 로직 단위 테스트 입니다.
 *
 * @author NohDongHui
 * @since 2025-04-04
 */
@DisplayName("EsMoveEventAnalyzer 테스트")
class EsMoveEventAnalyzerTest {

	private EsMoveEventAnalyzer analyzer;

	@BeforeEach
	void setUp() {
		MoveAnalyzerProperties props = new MoveAnalyzerProperties();
		props.setTimeWindowMs(3000L); // 3초 이내
		props.setTurnThreshold(4);    // 꺾임 4번 이상
		props.setAngleThresholdDegrees(90.0); // 90도 이상 꺾임 감지

		analyzer = new EsMoveEventAnalyzer(props);
	}

	private PointerMoveEventDocument createMoveEvent(LocalDateTime timestamp, int clientX, int clientY) {
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
	@DisplayName("지그재그 움직임이 감지되는 경우 outlier로 마킹됨")
	void testZigzagMovementDetected() {
		LocalDateTime now = LocalDateTime.now();

		List<PointerMoveEventDocument> events = List.of(
			createMoveEvent(now, 100, 100),        // 시작
			createMoveEvent(now.plusNanos(100_000_000), 110, 100), // →
			createMoveEvent(now.plusNanos(200_000_000), 110, 110), // ↓
			createMoveEvent(now.plusNanos(300_000_000), 100, 110), // ←
			createMoveEvent(now.plusNanos(400_000_000), 100, 100), // ↑
			createMoveEvent(now.plusNanos(500_000_000), 120, 100)  // →
		);

		analyzer.analyze(events);

		long outlierCount = events.stream().filter(PointerMoveEventDocument::getIsOutlier).count();
		assertThat(outlierCount).isGreaterThanOrEqualTo(4); // 일부만 마킹돼도 됨
	}

	@Test
	@DisplayName("꺾임 횟수가 부족하면 마킹되지 않음")
	void testZigzagMovementNotDetected_dueToLowTurnCount() {
		LocalDateTime now = LocalDateTime.now();

		List<PointerMoveEventDocument> events = List.of(
			createMoveEvent(now, 100, 100),
			createMoveEvent(now.plusNanos(100_000_000), 110, 100),
			createMoveEvent(now.plusNanos(200_000_000), 110, 110) // 2번 꺾임 → 부족
		);

		analyzer.analyze(events);

		assertThat(events.stream().anyMatch(PointerMoveEventDocument::getIsOutlier)).isFalse();
	}

	@Test
	@DisplayName("시간 간격이 너무 크면 윈도우가 리셋되어 감지 안 됨")
	void testZigzagMovementNotDetected_dueToTimeout() {
		LocalDateTime now = LocalDateTime.now();

		List<PointerMoveEventDocument> events = List.of(
			createMoveEvent(now, 100, 100),
			createMoveEvent(now.plusSeconds(5), 110, 100), // 5초 후 → time window 밖
			createMoveEvent(now.plusSeconds(6), 110, 110),
			createMoveEvent(now.plusSeconds(7), 100, 110),
			createMoveEvent(now.plusSeconds(8), 100, 100)
		);

		analyzer.analyze(events);

		assertThat(events.stream().anyMatch(PointerMoveEventDocument::getIsOutlier)).isFalse();
	}
}
