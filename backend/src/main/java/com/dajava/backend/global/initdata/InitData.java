package com.dajava.backend.global.initdata;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.entity.SolutionEventDocument;
import com.dajava.backend.domain.event.es.repository.PointerClickEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerMoveEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerScrollEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.event.es.repository.SolutionEventDocumentRepository;
import com.dajava.backend.domain.image.service.pageCapture.FileCleanupService;
import com.dajava.backend.domain.register.dto.pageCapture.PageCaptureRequest;
import com.dajava.backend.domain.register.dto.pageCapture.PageCaptureResponse;
import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.register.service.RegisterCacheService;
import com.dajava.backend.domain.register.service.RegisterService;
import com.dajava.backend.domain.solution.scheduler.SolutionScheduler;
import com.dajava.backend.global.utils.PasswordUtils;
import com.dajava.backend.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitData {

	@Value("${init.flag:1}")
	private int initFlag;

	private final FileCleanupService fileCleanupService;
	private final PointerClickEventDocumentRepository pointerClickEventDocumentRepository;
	private final PointerMoveEventDocumentRepository pointerMoveEventDocumentRepository;
	private final PointerScrollEventDocumentRepository pointerScrollEventDocumentRepository;
	private final RegisterService registerService;
	private final RegisterRepository registerRepository;
	private final SessionDataDocumentRepository sessionDataDocumentRepository;
	private final SolutionEventDocumentRepository solutionEventDocumentRepository;
	private final RegisterCacheService registerCacheService;
	private static final Random random = new Random();
	@Autowired
	private SolutionScheduler solutionScheduler;

	// -range 부터 +range 범위의 랜덤 오프셋을 반환합니다.
	public static int getRandomOffset(int range) {
		return random.nextInt(range * 2 + 1) - range;
	}

	private PointerClickEventDocument createEvent(Long time, int clientX, int clientY, String tag) {
		return PointerClickEventDocument.builder()
			.id(UUID.randomUUID().toString())
			.timestamp(time)
			.clientX(clientX)
			.clientY(clientY)
			.sessionId("AiSolutionTestSessionNumber")
			.pageUrl("https://www.dajava.link/main")
			.browserWidth(1920)
			.scrollHeight(2000)
			.viewportHeight(300)
			.scrollY(500)
			.memberSerialNumber("5_team_testSerial")
			.element(tag)
			.isOutlier(false)
			.build();
	}

	private PointerMoveEventDocument createMoveEvent(Long timestamp, int clientX, int clientY) {
		return PointerMoveEventDocument.builder()
			.id(UUID.randomUUID().toString())
			.timestamp(timestamp)
			.clientX(clientX)
			.clientY(clientY)
			.sessionId("AiSolutionTestSessionNumber")
			.pageUrl("https://www.dajava.link/main")
			.browserWidth(1920)
			.memberSerialNumber("5_team_testSerial")
			.scrollHeight(2000)
			.viewportHeight(300)
			.scrollY(500)
			.isOutlier(false)
			.build();
	}

	private PointerScrollEventDocument createScrollEvent(Long time, int scrollY, int scrollHeight, int viewportHeight) {
		return PointerScrollEventDocument.builder()
			.id(UUID.randomUUID().toString())
			.timestamp(time)
			.scrollY(scrollY)
			.scrollHeight(scrollHeight)
			.viewportHeight(viewportHeight)
			.pageUrl("https://www.dajava.link/main")
			.sessionId("AiSolutionTestSessionNumber")
			.memberSerialNumber("5_team_testSerial")
			.browserWidth(1920)
			.isOutlier(false)
			.build();
	}

	@Autowired
	@Lazy
	private InitData self;

	@Bean
	public ApplicationRunner baseInitDataApplicationRunner() {
		return args -> {
			if (initFlag != 1) {
				return;
			}

			cleanUp();
			self.work1();
			//self.work2();
			self.work3();
			self.work4();
			self.work5();
		};
	}

	public void cleanUp() {
		fileCleanupService.deleteNonLinkedFile();
	}

	@Transactional
	public void work1() {
		RegisterCreateRequest request = new RegisterCreateRequest(
			"example@example.com",
			"password123!",
			"https://mock.page",
			LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(0),
			LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0)
		);

		Register register = Register.builder()
			.serialNumber("5_team_testSerial")
			.email(request.email())
			.password(PasswordUtils.hashPassword(request.password()))
			.url(request.url())
			.startDate(request.startDate())
			.endDate(request.endDate())
			.duration(TimeUtils.getDuration(request.startDate(), request.endDate()))
			.isServiceExpired(true)
			.isSolutionComplete(false)
			.captureData(new ArrayList<>())
			.build();

		if (registerRepository.findBySerialNumber(register.getSerialNumber()).isEmpty()) {
			Register newRegister = registerRepository.save(register);
			registerCacheService.refreshCacheAll();
			log.info("baseInit register 등록 완료");
		}
	}

	public void work2() {

		// LocalDateTime now = LocalDateTime.now();
		// long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		//
		// SessionDataDocument sessionData = SessionDataDocument.create(
		// 	"AiSolutionTestSessionNumber",              // sessionId
		// 	"5_team_testSerial",                                 // memberSerialNumber
		// 	"https://www.dajava.link/main",                           // pageUrl
		// 	timestamp                                          // timestamp (2025-04-07T12:00:00Z 기준 millis)
		// );
		//
		// sessionDataDocumentRepository.save(sessionData);
		//
		// List<PointerClickEventDocument> clickEvents = List.of(
		// 	createEvent(timestamp, 100, 100, "button"),
		// 	createEvent(timestamp + 1, 102, 101, "button"),
		// 	createEvent(timestamp + 2, 103, 99, "button"),
		// 	createEvent(timestamp, 100, 200, "div"),
		// 	createEvent(timestamp, 200, 100, "button"),
		// 	createEvent(timestamp + 6000, 102, 101, "button"),
		// 	createEvent(timestamp + 12000, 103, 99, "button")
		// );
		//
		// pointerClickEventDocumentRepository.saveAll(clickEvents);
		//
		// List<PointerMoveEventDocument> moveEvents = List.of(
		// 	createMoveEvent(timestamp, 100, 100),        // 시작
		// 	createMoveEvent(timestamp + 100, 110, 100), // →
		// 	createMoveEvent(timestamp + 200, 110, 110), // ↓
		// 	createMoveEvent(timestamp + 300, 100, 110), // ←
		// 	createMoveEvent(timestamp + 400, 100, 100), // ↑
		// 	createMoveEvent(timestamp + 500, 120, 100),  // →
		// 	createMoveEvent(timestamp + 1000, 100, 100),
		// 	createMoveEvent(timestamp + 1100, 110, 100),
		// 	createMoveEvent(timestamp + 1200, 110, 110),
		// 	createMoveEvent(timestamp + 1300, 100, 100),
		// 	createMoveEvent(timestamp + 5000, 110, 100), // 5초 후 → time window 밖
		// 	createMoveEvent(timestamp + 6000, 110, 110),
		// 	createMoveEvent(timestamp + 7000, 100, 110),
		// 	createMoveEvent(timestamp + 8000, 100, 100)
		// );
		//
		// pointerMoveEventDocumentRepository.saveAll(moveEvents);
		//
		// List<PointerScrollEventDocument> scrollEvents = List.of(
		// 	createScrollEvent(timestamp, 100, 2000, 600),
		// 	createScrollEvent(timestamp + 100, 150, 2000, 600),
		// 	createScrollEvent(timestamp + 200, 100, 2000, 600),
		// 	createScrollEvent(timestamp + 300, 160, 2000, 600),
		// 	createScrollEvent(timestamp + 400, 110, 2000, 600),
		// 	createScrollEvent(timestamp, 1000, 3000, 600),
		// 	createScrollEvent(timestamp + 1100, 300, 3000, 600),
		// 	createScrollEvent(timestamp + 1200, 500, 3000, 600),
		// 	createScrollEvent(timestamp, 100, 2000, 600),
		// 	createScrollEvent(timestamp + 5000, 120, 2000, 600), // 시간 초과
		// 	createScrollEvent(timestamp + 10000, 1400, 2000, 600)
		// );
		//
		// pointerScrollEventDocumentRepository.saveAll(scrollEvents);
		//
		// log.info("baseInit 검증 로직용 테스트 데이터 등록 완료");
	}

	/**
	 * 테스트용 이미지 생성 로직
	 */
	public void work3() {
		ClassPathResource imageResource = new ClassPathResource("images/mock_page.jpeg");
		try (InputStream inputStream = imageResource.getInputStream()) {
			byte[] binaryData = inputStream.readAllBytes();
			String base64String = Base64.getEncoder().encodeToString(binaryData);

			MultipartFile multipartFile = new MockMultipartFile(
				"mock_page",
				"mock_page.jpeg",
				MediaType.IMAGE_JPEG_VALUE,
				base64String.getBytes(StandardCharsets.UTF_8)
			);

			PageCaptureRequest request = new PageCaptureRequest(
				"5_team_testSerial",           // 등록된 일련번호
				"https://mock.page",                        // 캡쳐 대상 페이지 URL
				multipartFile                                // 업로드할 이미지 파일
			);

			// 서비스 메서드를 직접 호출하여 초기 데이터를 생성
			PageCaptureResponse response = registerService.createPageCapture(request);
			log.info("목 페이지 이미지 저장 완료:{}", response.captureFileName());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gemini 솔루션 및 히트맵 생성용 데이터 생성 로직
	 * @author Metronon
	 * @since 2025-04-11
	 */
	public void work4() {
		// 공통적으로 사용되는 Fixed 값
		Integer browserWidth = 1920;
		Integer viewportHeight = 1080;
		Integer scrollHeight = 2671;
		String serialNumber = "5_team_testSerial";
		String pageUrl = "https://mock.page";
		String baseSessionId = "SolutionTestSessionId";

		LocalDateTime now = LocalDateTime.now();
		long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		// 세션 데이터 생성 (50개의 세션)
		for (int i = 0; i < 50; i++) {
			String sessionId = baseSessionId + i;
			SessionDataDocument sessionData = SessionDataDocument.create(
				sessionId,
				serialNumber,
				pageUrl,
				timestamp + i * 1000
			);
			sessionDataDocumentRepository.save(sessionData);
		}

		List<SolutionEventDocument> docs = new ArrayList<>();

		// 1. 중요도 우선순위에 따른 이벤트 분배
		// 검색창 (655, 614, 0) - 높은 중요도
		generateEventsByLocation(docs, 655, 614, 0, 500, 1000, 150, 30, 50, 15, baseSessionId, timestamp, pageUrl,
			serialNumber, browserWidth, viewportHeight, scrollHeight);

		// 로그인 버튼 (1743, 30, 0)
		generateEventsByLocation(docs, 1743, 30, 0, 300, 600, 100, 15, 10, 5, baseSessionId, timestamp, pageUrl,
			serialNumber, browserWidth, viewportHeight, scrollHeight);

		// 회원가입 버튼 (1800, 30, 0)
		generateEventsByLocation(docs, 1800, 30, 0, 200, 400, 70, 15, 10, 5, baseSessionId, timestamp, pageUrl,
			serialNumber, browserWidth, viewportHeight, scrollHeight);

		// 2. 상단 네비게이션 버튼들
		List<Integer> navButtonsX = Arrays.asList(658, 971, 1270, 1578);
		for (Integer x : navButtonsX) {
			generateEventsByLocation(docs, x, 75, 0, 75, 150, 25, 20, 10, 0, baseSessionId, timestamp, pageUrl,
				serialNumber, browserWidth, viewportHeight, scrollHeight);
		}

		// 3. 1열 상품 중심 좌표
		List<Integer> productRowX = Arrays.asList(250, 430, 610, 789, 973, 1159, 1358, 1525);
		for (Integer x : productRowX) {
			generateEventsByLocation(docs, x, 245, 700, 30, 60, 10, 30, 30, 5, baseSessionId, timestamp, pageUrl,
				serialNumber, browserWidth, viewportHeight, scrollHeight);
		}

		// 4. 하단 기타 상품영역 랜덤 이벤트 생성
		generateRandomEventsInArea(docs, 298, 1630, 64, 756, 1200, 500, baseSessionId, timestamp, pageUrl, serialNumber,
			browserWidth, viewportHeight, scrollHeight);

		// 총합이 5000개가 되도록 조정
		int currentTotal = docs.size();
		int remaining = 5000 - currentTotal;

		if (remaining > 0) {
			// 남은 이벤트는 전체 페이지 영역에 랜덤하게 분배
			generateRandomEventsInArea(docs, 0, browserWidth, 0, scrollHeight, 0, remaining, baseSessionId, timestamp,
				pageUrl, serialNumber, browserWidth, viewportHeight, scrollHeight);
		}

		solutionEventDocumentRepository.saveAll(docs);
		log.info("솔루션용 테스트 데이터 등록 완료: 총 {} 개 이벤트 생성", docs.size());
	}

	/**
	 * 특정 좌표 주변에 이벤트 생성
	 */
	private void generateEventsByLocation(
		List<SolutionEventDocument> docs,
		int centerX,
		int centerY,
		int scrollY,
		int clickCount,
		int moveCount,
		int scrollCount,
		int rangeX,
		int rangeY,
		int rangeScroll,
		String baseSessionId,
		long timestamp,
		String pageUrl,
		String serialNumber,
		int browserWidth,
		int viewportHeight,
		int scrollHeight
	) {
		// 클릭 이벤트 생성
		for (int i = 0; i < clickCount; i++) {
			int offsetX = getRandomOffset(rangeX);
			int offsetY = getRandomOffset(rangeY);
			int x = Math.min(Math.max(centerX + offsetX, 0), browserWidth - 1);
			int y = Math.min(Math.max(centerY + offsetY, 0), viewportHeight - 1);
			int actualScrollY = Math.min(Math.max(scrollY + getRandomOffset(rangeScroll), 0),
				scrollHeight - viewportHeight);

			String sessionId = baseSessionId + (i % 50);
			String element = determineElementType(centerX, centerY, scrollY);

			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId,
				pageUrl,
				"click",
				actualScrollY,
				scrollHeight,
				viewportHeight,
				browserWidth,
				timestamp + i * 10,
				x,
				y,
				element,
				serialNumber,
				i % 25 == 0 // 약 4%의 이상치
			);
			docs.add(doc);
		}

		// 이동 이벤트 생성
		for (int i = 0; i < moveCount; i++) {
			int offsetX = getRandomOffset(rangeX * 2);
			int offsetY = getRandomOffset(rangeY * 2);
			int x = Math.min(Math.max(centerX + offsetX, 0), browserWidth - 1);
			int y = Math.min(Math.max(centerY + offsetY, 0), viewportHeight - 1);
			int actualScrollY = Math.min(Math.max(scrollY + getRandomOffset(rangeScroll), 0),
				scrollHeight - viewportHeight);

			String sessionId = baseSessionId + (i % 50);

			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId,
				pageUrl,
				"move",
				actualScrollY,
				scrollHeight,
				viewportHeight,
				browserWidth,
				timestamp + i * 5,
				x,
				y,
				null,
				serialNumber,
				i % 30 == 0 // 약 3.3%의 이상치
			);
			docs.add(doc);
		}

		// 스크롤 이벤트 생성
		for (int i = 0; i < scrollCount; i++) {
			int offsetY = getRandomOffset(rangeScroll * 3);
			int actualScrollY = Math.min(Math.max(scrollY + offsetY, 0), scrollHeight - viewportHeight);

			String sessionId = baseSessionId + (i % 50);

			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId,
				pageUrl,
				"scroll",
				actualScrollY,
				scrollHeight,
				viewportHeight,
				browserWidth,
				timestamp + i * 15,
				centerX + getRandomOffset(rangeX),
				centerY + getRandomOffset(rangeY),
				null,
				serialNumber,
				i % 20 == 0 // 약 5%의 이상치
			);
			docs.add(doc);
		}
	}

	/**
	 * 특정 영역 내에서 랜덤 이벤트 생성
	 */
	private void generateRandomEventsInArea(
		List<SolutionEventDocument> docs,
		int minX,
		int maxX,
		int minY,
		int maxY,
		int baseScrollY,
		int totalEvents,
		String baseSessionId,
		long timestamp,
		String pageUrl,
		String serialNumber,
		int browserWidth,
		int viewportHeight,
		int scrollHeight
	) {
		// 클릭 : 이동 : 스크롤 = 1.5 : 3 : 0.5 비율
		int clickCount = (int)(totalEvents * 0.3);
		int scrollCount = (int)(totalEvents * 0.1);
		int moveCount = totalEvents - clickCount - scrollCount;

		// 클릭 이벤트
		for (int i = 0; i < clickCount; i++) {
			int x = random.nextInt(maxX - minX + 1) + minX;
			int y = random.nextInt(maxY - minY + 1) + minY;
			int actualScrollY = Math.min(Math.max(baseScrollY + getRandomOffset(100), 0),
				scrollHeight - viewportHeight);

			String sessionId = baseSessionId + (i % 50);
			String element = determineElementType(x, y, actualScrollY);

			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId,
				pageUrl,
				"click",
				actualScrollY,
				scrollHeight,
				viewportHeight,
				browserWidth,
				timestamp + i * 10 + 30000,
				x,
				y,
				element,
				serialNumber,
				i % 25 == 0
			);
			docs.add(doc);
		}

		// 이동 이벤트
		for (int i = 0; i < moveCount; i++) {
			int x = random.nextInt(maxX - minX + 1) + minX;
			int y = random.nextInt(maxY - minY + 1) + minY;
			int actualScrollY = Math.min(Math.max(baseScrollY + getRandomOffset(100), 0),
				scrollHeight - viewportHeight);

			String sessionId = baseSessionId + (i % 50);

			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId,
				pageUrl,
				"move",
				actualScrollY,
				scrollHeight,
				viewportHeight,
				browserWidth,
				timestamp + i * 5 + 40000,
				x,
				y,
				null,
				serialNumber,
				i % 30 == 0
			);
			docs.add(doc);
		}

		// 스크롤 이벤트
		for (int i = 0; i < scrollCount; i++) {
			int x = random.nextInt(maxX - minX + 1) + minX;
			int y = random.nextInt(maxY - minY + 1) + minY;
			int actualScrollY = Math.min(Math.max(baseScrollY + getRandomOffset(200), 0),
				scrollHeight - viewportHeight);

			String sessionId = baseSessionId + (i % 50);

			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId,
				pageUrl,
				"scroll",
				actualScrollY,
				scrollHeight,
				viewportHeight,
				browserWidth,
				timestamp + i * 15 + 50000,
				x,
				y,
				null,
				serialNumber,
				i % 20 == 0
			);
			docs.add(doc);
		}
	}

	/**
	 * 좌표와 스크롤 위치에 따라 요소 타입을 결정
	 */
	private String determineElementType(int x, int y, int scrollY) {
		// 검색창
		if (isInRange(x, y, 655, 614, 50, 30) && scrollY < 100) {
			return "input";
		}

		// 로그인/회원가입 버튼
		if ((isInRange(x, y, 1743, 30, 20, 15) || isInRange(x, y, 1800, 30, 20, 15)) && scrollY < 100) {
			return "button";
		}

		// 네비게이션 버튼
		if ((isInRange(x, y, 658, 75, 30, 15) ||
			isInRange(x, y, 971, 75, 30, 15) ||
			isInRange(x, y, 1270, 75, 30, 15) ||
			isInRange(x, y, 1578, 75, 30, 15)) && scrollY < 100) {
			return "a";
		}

		// 상품 영역
		if (scrollY >= 600 && scrollY <= 800) {
			return "img";
		}

		// 하단 상품 영역
		if (scrollY >= 1100) {
			return "div";
		}

		// 기본 요소 타입
		String[] elementTypes = {"div", "span", "button", "a", "img"};
		return elementTypes[random.nextInt(elementTypes.length)];
	}

	/**
	 * 좌표가 특정 영역 내에 있는지 확인
	 */
	private boolean isInRange(int x, int y, int centerX, int centerY, int rangeX, int rangeY) {
		return x >= (centerX - rangeX) && x <= (centerX + rangeX) &&
			y >= (centerY - rangeY) && y <= (centerY + rangeY);
	}

	public void work5() {
		solutionScheduler.processExpiredRegisters();
		log.info("AI 솔루션 결과 생성 완료 (Gemini)");
	}
}
