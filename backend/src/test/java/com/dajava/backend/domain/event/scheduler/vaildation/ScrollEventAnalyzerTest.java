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

import com.dajava.backend.domain.event.entity.PointerScrollEvent;
import com.dajava.backend.global.common.BaseTimeEntity;

/*
 * 스크롤 이벤트 분석 단위 테스트 입니다.
 *
 * @author NohDongHui
 * @since 2025-03-25
 */
public class ScrollEventAnalyzerTest {

	private ScrollEventAnalyzer analyzer;

	@BeforeEach
	void setUp() {
		analyzer = new ScrollEventAnalyzer(
			3000,
			300,
			3,
			3,
			3,
			2000
		);
	}

	private PointerScrollEvent testScrollEvent(Instant createDate, int scrollY) {
		PointerScrollEvent event = PointerScrollEvent.builder()
			.scrollY(scrollY)
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
	@DisplayName("rageScroll이 일어난 경우 테스트")
	void t1() {

		// 3초 내에 scrollY가 크게 움직이는 이벤트들 생성
		Instant now = Instant.now();
		List<PointerScrollEvent> events = List.of(
			testScrollEvent(now, 100),
			testScrollEvent(now.plusMillis(100), 101),
			testScrollEvent(now.plusMillis(100), 500),
			testScrollEvent(now.plusMillis(100), 600),
			testScrollEvent(now.plusMillis(100), 610),
			testScrollEvent(now.plusMillis(100), 1200),
			testScrollEvent(now.plusMillis(100), 1210),
			testScrollEvent(now.plusMillis(100), 1220),
			testScrollEvent(now.plusMillis(100), 1800)
		);

		// when
		List<PointerScrollEvent> result = analyzer.countRageScrollBursts(events);

		// then
		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("rageScroll이 일어나지 않은 경우 테스트")
	void t2() {

		// 3초 내에 scrollY가 크게 움직이는 이벤트들 생성
		Instant now = Instant.now();
		List<PointerScrollEvent> events = List.of(
			testScrollEvent(now, 100),
			testScrollEvent(now.plusMillis(100), 101)
		);

		// when
		List<PointerScrollEvent> result = analyzer.countRageScrollBursts(events);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("방향 전환 감지 테스트 참인 경우")
	void t3() {

		Instant now = Instant.now();
		List<PointerScrollEvent> events = List.of(
			testScrollEvent(now, 100),
			testScrollEvent(now.plusMillis(100), 101),
			testScrollEvent(now.plusMillis(100), 90),
			testScrollEvent(now.plusMillis(100), 100),
			testScrollEvent(now.plusMillis(100), 90),
			testScrollEvent(now.plusMillis(100), 80),
			testScrollEvent(now.plusMillis(100), 100)
		);

		List<PointerScrollEvent> result = analyzer.getBackAndForthScrollOutliers(events);

		assertThat(result).isNotEmpty();

	}

	@Test
	@DisplayName("방향 전환 감지 테스트 거짓인 경우")
	void t4() {

		Instant now = Instant.now();
		List<PointerScrollEvent> events = List.of(
			testScrollEvent(now, 100),
			testScrollEvent(now.plusMillis(100), 101),
			testScrollEvent(now.plusMillis(100), 102),
			testScrollEvent(now.plusMillis(100), 103)

		);

		List<PointerScrollEvent> result = analyzer.getBackAndForthScrollOutliers(events);

		assertThat(result).isEmpty();

	}

	@Test
	@DisplayName("사용자가 윗 스크롤에만 머무르는 경우 검출")
	void t5() {

		Instant now = Instant.now();
		List<PointerScrollEvent> events = List.of(
			testScrollEvent(now, 100),
			testScrollEvent(now.plusMillis(100), 500),
			testScrollEvent(now.plusMillis(100), 1000),
			testScrollEvent(now.plusMillis(100), 1500)

		);

		List<PointerScrollEvent> result = analyzer.getTopRepeatScrollOutliers(events);

		assertThat(result).isNotEmpty();

	}
}
