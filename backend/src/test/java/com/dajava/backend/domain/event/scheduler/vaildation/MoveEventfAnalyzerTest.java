package com.dajava.backend.domain.event.scheduler.vaildation;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.event.entity.PointerMoveEvent;
import com.dajava.backend.global.common.BaseTimeEntity;

public class MoveEventfAnalyzerTest {

	private MoveEventAnalyzer analyzer;

	@BeforeEach
	void setUp() {
		analyzer = new MoveEventAnalyzer(
		3000,
		4,
		90.0);
	}

	private PointerMoveEvent testMoveEvent(Instant createDate, int clientX, int clientY) {
		PointerMoveEvent event = PointerMoveEvent.builder()
			.clientX(clientX)
			.clientY(clientY)
			.pageUrl("/test")
			.browserWidth(1920)
			.sessionId("test-session")
			.memberSerialNumber("member-123")
			.sessionData(null)
			.build();

		// BaseTimeEntity의 private createDate 설정
		try {
			Field field = BaseTimeEntity.class.getDeclaredField("createDate");
			field.setAccessible(true);
			field.set(event, LocalDateTime.ofInstant(createDate, ZoneId.systemDefault()));
		} catch (Exception e) {
			throw new RuntimeException("createDate 설정 실패", e);
		}

		return event;
	}

	@Test
	@DisplayName("지그재그 무빙 일어난 경우")
	void t1() {
		Instant now = Instant.now();
		List<PointerMoveEvent> events = List.of(
			testMoveEvent(now, 100, 100), // 시작
			testMoveEvent(now.plusMillis(100), 110, 100), // → 오른쪽
			testMoveEvent(now.plusMillis(200), 110, 110), // ↓ 아래로 꺾임 (90°)
			testMoveEvent(now.plusMillis(300), 100, 110), // ← 왼쪽으로 꺾임 (90°)
			testMoveEvent(now.plusMillis(400), 100, 100), // ↑ 위로 꺾임 (90°)
			testMoveEvent(now.plusMillis(500), 120, 100)  // → 오른쪽 (꺾임 4회 이상)
		);

		List<PointerMoveEvent> result = analyzer.detectZigzagMovementByAngle(events);
		assertThat(result).isNotEmpty();
	}
}
