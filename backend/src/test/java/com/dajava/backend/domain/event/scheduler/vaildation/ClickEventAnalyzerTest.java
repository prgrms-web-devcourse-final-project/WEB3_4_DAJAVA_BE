package com.dajava.backend.domain.event.scheduler.vaildation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.event.PointerClickEvent;
import com.dajava.backend.global.common.BaseTimeEntity;

/*
 * 클릭 이벤트 분석 단위 테스트 입니다.
 *
 * @author NohDongHui
 * @since 2025-03-25
 */
public class ClickEventAnalyzerTest {

	private ClickEventAnalyzer analyzer;

	@BeforeEach
	void setUp() {
		analyzer = new ClickEventAnalyzer();
	}

	private PointerClickEvent testClickEvent(Instant createDate, int clientX, int clientY) {
		PointerClickEvent event = PointerClickEvent.builder()
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
	@DisplayName("rageClick이 일어난 경우 테스트")
	void t1() {
		Instant now = Instant.now();
		List<PointerClickEvent> events = List.of(
			testClickEvent(now, 100, 100),
			testClickEvent(now.plusMillis(1000), 102, 101),
			testClickEvent(now.plusMillis(2000), 103, 99)
		);

		assertTrue(analyzer.detectRageClicks(events));
	}

	@Test
	@DisplayName("rageClick이 일어나지 않은 경우 테스트 (기준 시간 안에 여러번 클릭 x)")
	void t2() {
		Instant now = Instant.now();
		List<PointerClickEvent> events = List.of(
			testClickEvent(now, 100, 100),
			testClickEvent(now.plusMillis(6000), 102, 101),
			testClickEvent(now.plusMillis(12000), 103, 99)
		);

		assertFalse(analyzer.detectRageClicks(events));
	}

	@Test
	@DisplayName("rageClick이 일어나지 않은 경우 테스트 (기준 범위를 넘는 경우)")
	void detectRageClicks_withClicksOutsideProximity_returnsFalse() {
		Instant now = Instant.now();
		List<PointerClickEvent> events = List.of(
			testClickEvent(now, 100, 100),
			testClickEvent(now.plusMillis(1000), 150, 150),
			testClickEvent(now.plusMillis(2000), 200, 200)
		);

		assertFalse(analyzer.detectRageClicks(events));
	}

	@Test
	@DisplayName("rageClick이 일어나지 않은 경우 테스트 (빠르게 눌렀지만 기준보다 적게 누른 경우)")
	void detectRageClicks_withLessThanMinClickCount_returnsFalse() {
		Instant now = Instant.now();
		List<PointerClickEvent> events = List.of(
			testClickEvent(now, 100, 100),
			testClickEvent(now.plusMillis(1000), 102, 101)
		);

		assertFalse(analyzer.detectRageClicks(events));
	}



	//@Test
	void detectSuspiciousClicks_shouldReturnTrue_whenClickTagIsDivWithoutOnClick() {

		PointerClickEvent event = PointerClickEvent.builder()
			.clientX(100)
			.clientY(200)
			.pageUrl("/test")
			.browserWidth(1920)
			.sessionId("test-session")
			.memberSerialNumber("member-123")
			//.clickTag("div") // clickTag가 "div"이므로 의심 클릭 대상
			.build();

		// when
		boolean result = analyzer.detectSuspiciousClicks(List.of(event));

		// then
		assertThat(result).isTrue(); // div는 SUSPICIOUS_TAGS에 있고, onclick도 없으므로 true
	}
}
