package com.dajava.backend.domain.event.scheduler.vaildation;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.event.entity.PointerClickEvent;
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
		analyzer = new ClickEventAnalyzer(
			5000,
			10,
			3);
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

		assertThat(analyzer.getRageClicks(events)).isNotEmpty();
	}

	@Test
	@DisplayName("rageClick이 일어나지 않은 경우 테스트 (시간 초과)")
	void t2() {
		Instant now = Instant.now();
		List<PointerClickEvent> events = List.of(
			testClickEvent(now, 100, 100),
			testClickEvent(now.plusMillis(6000), 102, 101),
			testClickEvent(now.plusMillis(12000), 103, 99)
		);

		assertThat(analyzer.getRageClicks(events)).isEmpty();
	}

	@Test
	@DisplayName("rageClick이 일어나지 않은 경우 테스트 (위치 기준 초과)")
	void detectRageClicks_withClicksOutsideProximity_returnsFalse() {
		Instant now = Instant.now();
		List<PointerClickEvent> events = List.of(
			testClickEvent(now, 100, 100),
			testClickEvent(now.plusMillis(1000), 150, 150),
			testClickEvent(now.plusMillis(2000), 200, 200)
		);

		assertThat(analyzer.getRageClicks(events)).isEmpty();
	}

	@Test
	@DisplayName("rageClick이 일어나지 않은 경우 테스트 (클릭 수 부족)")
	void detectRageClicks_withLessThanMinClickCount_returnsFalse() {
		Instant now = Instant.now();
		List<PointerClickEvent> events = List.of(
			testClickEvent(now, 100, 100),
			testClickEvent(now.plusMillis(1000), 102, 101)
		);

		assertThat(analyzer.getRageClicks(events)).isEmpty();
	}

	@Test
	@DisplayName("의심 클릭이 감지되는 경우 테스트")
	void detectSuspiciousClicks_shouldReturnTrue_whenClickTagIsDivWithoutOnClick() {
		PointerClickEvent event = PointerClickEvent.builder()
			.clientX(100)
			.clientY(200)
			.pageUrl("/test")
			.browserWidth(1920)
			.sessionId("test-session")
			.memberSerialNumber("member-123")
			// .clickTag("div") → 나중에 구현되면 반영
			.build();

		List<PointerClickEvent> result = analyzer.getSuspiciousClicks(List.of(event));

		assertThat(result).isNotEmpty();
	}
}
