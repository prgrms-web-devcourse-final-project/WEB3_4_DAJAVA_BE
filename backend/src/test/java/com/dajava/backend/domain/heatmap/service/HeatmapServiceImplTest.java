package com.dajava.backend.domain.heatmap.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.entity.SolutionEvent;
import com.dajava.backend.domain.event.repository.SolutionDataRepository;
import com.dajava.backend.domain.heatmap.dto.HeatmapResponse;
import com.dajava.backend.domain.heatmap.exception.HeatmapException;
import com.dajava.backend.domain.register.entity.PageCaptureData;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.global.utils.PasswordUtils;

@ExtendWith(MockitoExtension.class)
class HeatmapServiceImplTest {

	@Mock
	private RegisterRepository registerRepository;

	@Mock
	private SolutionDataRepository solutionDataRepository;

	@Mock
	private PasswordUtils passwordUtils;

	@InjectMocks
	private HeatmapServiceImpl heatmapService;

	private Register register;
	private SolutionData mockSolutionData;
	private List<SolutionEvent> mockEvents;

	@BeforeEach
	void setUp() {
		// Register
		register = Register.builder()
			.serialNumber("5_team_testSerial")
			.password("password123!")
			.url("http://localhost:3000/myPage1")
			.captureData(new ArrayList<>())
			.build();

		List<PageCaptureData> PageCaptureList = new ArrayList<>();
		PageCaptureList.add(
			PageCaptureData.builder()
				.pageUrl("http://localhost:3000/myPage1")
				.pageCapturePath("/page-capture/sample1.png")
				.register(register)
				.build()
		);
		PageCaptureList.add(
			PageCaptureData.builder()
				.pageUrl("http://localhost:3000/myPage2")
				.pageCapturePath("/page-capture/sample2.png")
				.register(register)
				.build()
		);

		register.getCaptureData().addAll(PageCaptureList);
		registerRepository.save(register);

		// 이벤트 리스트 초기화
		mockEvents = new ArrayList<>();

		// 클릭 이벤트 생성
		SolutionEvent clickEvent = SolutionEvent.builder()
			.type("click")
			.clientX(100)
			.clientY(200)
			.scrollY(50)
			.browserWidth(1200)
			.scrollHeight(3000)
			.sessionId("session1")
			.pageUrl("http://localhost:3000/myPage1")
			.timestamp(LocalDateTime.now().minusDays(4))
			.build();
		mockEvents.add(clickEvent);

		// 이동 이벤트 생성
		SolutionEvent mousemoveEvent = SolutionEvent.builder()
			.type("mousemove")
			.clientX(150)
			.clientY(250)
			.scrollY(50)
			.browserWidth(1200)
			.scrollHeight(3000)
			.sessionId("session1")
			.pageUrl("http://localhost:3000/myPage1")
			.timestamp(LocalDateTime.now().minusDays(4))
			.build();
		mockEvents.add(mousemoveEvent);

		// 스크롤 이벤트 생성
		for (int i = 0; i < 5; i++) {
			SolutionEvent scrollEvent = SolutionEvent.builder()
				.type("scroll")
				.scrollY(i * 100)
				.viewportHeight(800)
				.browserWidth(1200)
				.scrollHeight(3000)
				.sessionId("session" + (i % 2 + 1))
				.pageUrl("http://localhost:3000/myPage1")
				.timestamp(LocalDateTime.now().minusDays(4).minusMinutes(20 - i))
				.build();
			mockEvents.add(scrollEvent);
		}

		// SolutionData 객체 생성
		mockSolutionData = SolutionData.builder()
			.solutionEvents(mockEvents)
			.build();

		solutionDataRepository.save(mockSolutionData);
	}

	@Test
	@DisplayName("1. 유효한 클릭 타입의 히트맵 가져오기 테스트")
	void t001() {
		// Given
		String serialNumber = "5_team_testSerial";
		String password = "password123!";
		String type = "click";

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			when(registerRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(register));

			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(true);

			when(solutionDataRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(mockSolutionData));

			// When
			HeatmapResponse response = heatmapService.getHeatmap(serialNumber, password, type);

			// Then
			assertNotNull(response);
			assertEquals(10, response.gridSize());
			assertEquals(1200, response.pageWidth());
			assertEquals(3000, response.pageHeight());
			assertEquals("/page-capture/sample1.png", response.pageCapture());
			assertNotNull(response.gridCells());
			assertNotNull(response.metadata());
		}
	}

	@Test
	@DisplayName("2. 유효한 마우스무브 타입의 히트맵 가져오기 테스트")
	void t002() {
		// Given
		String serialNumber = "5_team_testSerial";
		String password = "password123!";
		String type = "mousemove";

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			when(registerRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(register));

			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(true);

			when(solutionDataRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(mockSolutionData));

			// When
			HeatmapResponse response = heatmapService.getHeatmap(serialNumber, password, type);

			// Then
			assertNotNull(response);
			assertEquals(10, response.gridSize());
			assertNotNull(response.gridCells());
		}
	}

	@Test
	@DisplayName("3. 유효한 스크롤 타입의 히트맵 가져오기 테스트")
	void t003() {
		// Given
		String serialNumber = "5_team_testSerial";
		String password = "password123!";
		String type = "scroll";

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			when(registerRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(register));

			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(true);

			when(solutionDataRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(mockSolutionData));

			// When
			HeatmapResponse response = heatmapService.getHeatmap(serialNumber, password, type);

			// Then
			assertNotNull(response);
			assertEquals(10, response.gridSize());
			assertNotNull(response.gridCells());
		}
	}

	@Test
	@DisplayName("4. 잘못된 시리얼 번호로 조회 시 예외 발생 테스트")
	void t004() {
		// Given
		String serialNumber = "INVALID_SN";
		String password = "password123!";
		String type = "click";

		when(registerRepository.findBySerialNumber(serialNumber))
			.thenReturn(Optional.empty());

		// When & Then
		assertThrows(HeatmapException.class, () -> heatmapService.getHeatmap(serialNumber, password, type));
		verify(registerRepository).findBySerialNumber(serialNumber);
		verify(solutionDataRepository, never()).findBySerialNumber(any());
	}

	@Test
	@DisplayName("5. 잘못된 비밀번호로 조회 시 예외 발생 테스트")
	void t005() {
		// Given
		String serialNumber = "5_team_testSerial";
		String password = "wrong_password";
		String type = "click";

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			when(registerRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(register));

			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(false);

			// When & Then
			assertThrows(HeatmapException.class, () -> heatmapService.getHeatmap(serialNumber, password, type));
			verify(registerRepository).findBySerialNumber(serialNumber);
			// 비밀번호 검증 실패 시, solutionDataRepository 는 호출되지 않아야 함
			verify(solutionDataRepository, never()).findBySerialNumber(any());
		}
	}

	@Test
	@DisplayName("6. 솔루션 데이터가 없을 때 예외 발생 테스트")
	void t006() {
		// Given
		String serialNumber = "5_team_testSerial";
		String password = "password123!";
		String type = "click";

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			when(registerRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(register));

			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(true);

			when(solutionDataRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.empty());

			// When & Then
			assertThrows(HeatmapException.class, () -> heatmapService.getHeatmap(serialNumber, password, type));
			verify(solutionDataRepository).findBySerialNumber(serialNumber);
		}
	}

	@Test
	@DisplayName("7. 이벤트 데이터가 없을 때 예외 발생 테스트")
	void t007() {
		// Given
		String serialNumber = "5_team_testSerial";
		String password = "password123!";
		String type = "click";

		// 빈 이벤트 리스트를 가진 SolutionData 객체 생성
		SolutionData emptyData = SolutionData.builder()
			.solutionEvents(new ArrayList<>())
			.build();

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			when(registerRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(register));

			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(true);

			when(solutionDataRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(emptyData));

			// When & Then
			assertThrows(HeatmapException.class, () -> heatmapService.getHeatmap(serialNumber, password, type));
			verify(solutionDataRepository).findBySerialNumber(serialNumber);
		}
	}

	@Test
	@DisplayName("8. 잘못된 이벤트 타입으로 조회 시 예외 발생 테스트")
	void t008() {
		// Given
		String serialNumber = "5_team_testSerial";
		String password = "password123!";
		String type = "invalid_type";

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			when(registerRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(register));

			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(true);

			when(solutionDataRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(mockSolutionData));

			// When & Then
			assertThrows(HeatmapException.class, () -> heatmapService.getHeatmap(serialNumber, password, type));
			verify(solutionDataRepository).findBySerialNumber(serialNumber);
		}
	}

	@Test
	@DisplayName("9. 이벤트 수가 1000개 이상일 때 샘플링 적용 테스트")
	void t009() {
		// Given
		String serialNumber = "5_team_testSerial";
		String password = "password123!";
		String type = "click";

		// 1000개 이상의 이벤트 생성
		List<SolutionEvent> largeEventList = new ArrayList<>();
		for (int i = 0; i < 1500; i++) {
			SolutionEvent event = SolutionEvent.builder()
				.type("click")
				.clientX(i % 200)
				.clientY(i % 300)
				.scrollY(i % 100)
				.browserWidth(1200)
				.scrollHeight(3000)
				.sessionId("session" + (i % 5))
				.pageUrl("http://localhost:3000/myPage1")
				.timestamp(LocalDateTime.now().minusMinutes(i))
				.build();
			largeEventList.add(event);
		}

		SolutionData largeData = SolutionData.builder()
			.solutionEvents(largeEventList)
			.build();

		when(registerRepository.findBySerialNumber(serialNumber))
			.thenReturn(Optional.of(register));

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			when(registerRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(register));

			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(true);

			when(solutionDataRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(largeData));

			// When
			HeatmapResponse response = heatmapService.getHeatmap(serialNumber, password, type);

			// Then
			assertNotNull(response);
			assertEquals(10, response.gridSize());
			assertNotNull(response.gridCells());
			assertNotNull(response.metadata());
			assertTrue(response.metadata().totalEvents() == 750); // 클릭은 2:1 샘플링 적용
			assertEquals("/page-capture/sample1.png", response.pageCapture());
		}
	}
}
