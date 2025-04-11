package com.dajava.backend.domain.heatmap.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.data.domain.Pageable;

import com.dajava.backend.domain.event.es.entity.SolutionEventDocument;
import com.dajava.backend.domain.event.es.repository.SolutionEventDocumentRepository;
import com.dajava.backend.domain.heatmap.dto.HeatmapResponse;
import com.dajava.backend.domain.heatmap.exception.HeatmapException;
import com.dajava.backend.domain.heatmap.validation.UrlEqualityValidator;
import com.dajava.backend.domain.register.entity.PageCaptureData;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.global.utils.PasswordUtils;

@ExtendWith(MockitoExtension.class)
class HeatmapServiceImplTest {

	@Mock
	private RegisterRepository registerRepository;

	// ElasticSearch에서 이벤트 document를 직접 조회하는 Repository
	@Mock
	private SolutionEventDocumentRepository solutionEventDocumentRepository;

	@Mock
	private UrlEqualityValidator urlEqualityValidator;

	@Mock
	private PasswordUtils passwordUtils;

	@InjectMocks
	private HeatmapServiceImpl heatmapService;

	private Register register;
	private List<SolutionEventDocument> mockDocuments;

	@BeforeEach
	void setUp() {
		// Register 객체 초기화
		register = Register.builder()
			.serialNumber("5_team_testSerial")
			.password("password123!")
			.url("http://localhost:3000/myPage1")
			.captureData(new ArrayList<>())
			.build();

		List<PageCaptureData> pageCaptureList = new ArrayList<>();
		pageCaptureList.add(
			PageCaptureData.builder()
				.pageUrl("http://localhost:3000/myPage1")
				.captureFileName("sample1.png")
				.register(register)
				.build()
		);
		pageCaptureList.add(
			PageCaptureData.builder()
				.pageUrl("http://localhost:3000/myPage2")
				.captureFileName("sample2.png")
				.register(register)
				.build()
		);
		register.getCaptureData().addAll(pageCaptureList);
		registerRepository.save(register);

		// ElasticSearch에 저장된 이벤트 Document들을 생성
		mockDocuments = new ArrayList<>();

		// 클릭 이벤트 document (timestamp: 밀리초 단위)
		long clickTimestamp = LocalDateTime.now()
			.minusDays(4)
			.atZone(ZoneId.systemDefault())
			.toInstant()
			.toEpochMilli();
		SolutionEventDocument clickDoc = SolutionEventDocument.builder()
			.type("click")
			.clientX(100)
			.clientY(200)
			.scrollY(50)
			.browserWidth(1200)
			.scrollHeight(3000)
			.sessionId("session1")
			.pageUrl("http://localhost:3000/myPage1")
			.timestamp(clickTimestamp)
			.build();
		mockDocuments.add(clickDoc);

		// 마우스무브 이벤트 document
		long mousemoveTimestamp = LocalDateTime.now()
			.minusDays(4)
			.atZone(ZoneId.systemDefault())
			.toInstant()
			.toEpochMilli();
		SolutionEventDocument mousemoveDoc = SolutionEventDocument.builder()
			.type("mousemove")
			.clientX(150)
			.clientY(250)
			.scrollY(50)
			.browserWidth(1200)
			.scrollHeight(3000)
			.sessionId("session1")
			.pageUrl("http://localhost:3000/myPage1")
			.timestamp(mousemoveTimestamp)
			.build();
		mockDocuments.add(mousemoveDoc);

		// 스크롤 이벤트 document 5개 생성
		for (int i = 0; i < 5; i++) {
			long scrollTimestamp = LocalDateTime.now()
				.minusDays(4)
				.minusMinutes(20 - i)
				.atZone(ZoneId.systemDefault())
				.toInstant()
				.toEpochMilli();
			SolutionEventDocument scrollDoc = SolutionEventDocument.builder()
				.type("scroll")
				.scrollY(i * 100)
				.viewportHeight(800)
				.browserWidth(1200)
				.scrollHeight(3000)
				.sessionId("session" + (i % 2 + 1))
				.pageUrl("http://localhost:3000/myPage1")
				.timestamp(scrollTimestamp)
				.build();
			mockDocuments.add(scrollDoc);
		}
	}

	@Test
	@DisplayName("1. 유효한 클릭 타입의 히트맵 가져오기 테스트")
	void t001() {
		// Given
		String serialNumber = "5_team_testSerial";
		String password = "password123!";
		String type = "click";
		String targetUrl = "http://localhost:3000/myPage1";

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			when(registerRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(register));
			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(true);
			// 페이징을 위해 첫 호출에 mockDocuments, 이후는 빈 리스트로 종료
			when(solutionEventDocumentRepository.findBySerialNumber(eq(serialNumber), any(Pageable.class)))
				.thenReturn(mockDocuments)
				.thenReturn(Collections.emptyList());

			// URL 비교 메서드 stubs 추가
			when(urlEqualityValidator.isMatching(eq(targetUrl), anyString())).thenReturn(true);

			// When
			HeatmapResponse response = heatmapService.getHeatmap(serialNumber, password, type);

			// Then
			assertNotNull(response);
			assertEquals(10, response.gridSize());
			assertEquals(1200, response.pageWidth());
			assertEquals(3000, response.pageHeight());
			assertEquals("sample1.png", response.pageCapture());
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
		String targetUrl = "http://localhost:3000/myPage1";

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			when(registerRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(register));
			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(true);
			when(solutionEventDocumentRepository.findBySerialNumber(eq(serialNumber), any(Pageable.class)))
				.thenReturn(mockDocuments)
				.thenReturn(Collections.emptyList());

			// URL 비교 메서드 stubs 추가
			when(urlEqualityValidator.isMatching(eq(targetUrl), anyString())).thenReturn(true);

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
		String targetUrl = "http://localhost:3000/myPage1";

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			when(registerRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(register));
			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(true);
			when(solutionEventDocumentRepository.findBySerialNumber(eq(serialNumber), any(Pageable.class)))
				.thenReturn(mockDocuments)
				.thenReturn(Collections.emptyList());

			// URL 비교 메서드 stubs 추가
			when(urlEqualityValidator.isMatching(eq(targetUrl), anyString())).thenReturn(true);

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
		verify(solutionEventDocumentRepository, never()).findBySerialNumber(any(), any());
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
			verify(solutionEventDocumentRepository, never()).findBySerialNumber(any(), any());
		}
	}

	@Test
	@DisplayName("6. 솔루션 데이터(이벤트 Document)가 없을 때 예외 발생 테스트")
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
			// 이벤트 Document가 하나도 없는 경우(emptyList)
			when(solutionEventDocumentRepository.findBySerialNumber(eq(serialNumber), any(Pageable.class)))
				.thenReturn(Collections.emptyList());

			// When & Then
			assertThrows(HeatmapException.class, () -> heatmapService.getHeatmap(serialNumber, password, type));
			verify(solutionEventDocumentRepository).findBySerialNumber(eq(serialNumber), any(Pageable.class));
		}
	}

	@Test
	@DisplayName("7. 이벤트 데이터가 없을 때 예외 발생 테스트")
	void t007() {
		// Given
		String serialNumber = "5_team_testSerial";
		String password = "password123!";
		String type = "click";

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			when(registerRepository.findBySerialNumber(serialNumber))
				.thenReturn(Optional.of(register));
			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(true);
			// 페이징 결과로 빈 리스트가 반환되는 경우
			when(solutionEventDocumentRepository.findBySerialNumber(eq(serialNumber), any(Pageable.class)))
				.thenReturn(Collections.emptyList());

			// When & Then
			assertThrows(HeatmapException.class, () -> heatmapService.getHeatmap(serialNumber, password, type));
			verify(solutionEventDocumentRepository).findBySerialNumber(eq(serialNumber), any(Pageable.class));
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
			when(solutionEventDocumentRepository.findBySerialNumber(eq(serialNumber), any(Pageable.class)))
				.thenReturn(mockDocuments)
				.thenReturn(Collections.emptyList());

			// URL 비교 메서드 stubbing은 제거 (실제로 호출되지 않음)

			// When & Then
			assertThrows(HeatmapException.class, () -> heatmapService.getHeatmap(serialNumber, password, type));

			// 페이징으로 인해 2번 호출됨
			verify(solutionEventDocumentRepository, times(2))
				.findBySerialNumber(eq(serialNumber), any(Pageable.class));
		}
	}

	@Test
	@DisplayName("9. 이벤트 수가 1000개 이상일 때 샘플링 적용 테스트")
	void t009() {
		// Given
		String serialNumber = "5_team_testSerial";
		String password = "password123!";
		String type = "click";
		String targetUrl = "http://localhost:3000/myPage1";

		// 1500개의 클릭 이벤트 Document 생성 (timestamp: 밀리초 단위)
		List<SolutionEventDocument> largeEventDocs = new ArrayList<>();
		for (int i = 0; i < 1500; i++) {
			long eventTimestamp = LocalDateTime.now().minusMinutes(i)
				.atZone(ZoneId.systemDefault())
				.toInstant()
				.toEpochMilli();
			SolutionEventDocument eventDoc = SolutionEventDocument.builder()
				.type("click")
				.clientX(i % 200)
				.clientY(i % 300)
				.scrollY(i % 100)
				.browserWidth(1200)
				.scrollHeight(3000)
				.sessionId("session" + (i % 5))
				.pageUrl("http://localhost:3000/myPage1")
				.timestamp(eventTimestamp)
				.build();
			largeEventDocs.add(eventDoc);
		}

		when(registerRepository.findBySerialNumber(serialNumber))
			.thenReturn(Optional.of(register));

		try (MockedStatic<PasswordUtils> passwordUtilsMock = mockStatic(PasswordUtils.class)) {
			passwordUtilsMock.when(() -> PasswordUtils.verifyPassword(password, register.getPassword()))
				.thenReturn(true);
			// 페이징: 첫 호출에 1500개의 Document, 이후 빈 리스트 반환으로 종료
			when(solutionEventDocumentRepository.findBySerialNumber(eq(serialNumber), any(Pageable.class)))
				.thenReturn(largeEventDocs)
				.thenReturn(Collections.emptyList());

			// URL 비교 메서드 stubs 추가
			when(urlEqualityValidator.isMatching(eq(targetUrl), anyString())).thenReturn(true);

			// When
			HeatmapResponse response = heatmapService.getHeatmap(serialNumber, password, type);

			// Then
			assertNotNull(response);
			assertEquals(10, response.gridSize());
			assertNotNull(response.gridCells());
			assertNotNull(response.metadata());
			// 1500개의 click 이벤트에 대해 2:1 샘플링 적용하면 총 이벤트 수는 750개여야 함
			assertEquals(750, response.metadata().totalEvents());
			assertEquals("sample1.png", response.pageCapture());
		}
	}
}
