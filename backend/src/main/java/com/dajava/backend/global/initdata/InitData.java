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
		Integer scrollHeight = 2617;
		String serialNumber = "5_team_testSerial";
		String pageUrl = "https://mock.page";
		String sessionId = "SolutionTestSessionId";

		LocalDateTime now = LocalDateTime.now();
		long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		SessionDataDocument sessionData = SessionDataDocument.create(
			sessionId,                                                           // sessionId
			serialNumber,                                                        // memberSerialNumber
			pageUrl,                                                             // pageUrl
			timestamp                                                            // timestamp
		);

		sessionDataDocumentRepository.save(sessionData);

		List<SolutionEventDocument> docs = new ArrayList<>();

		// 우상단 네비게이션 버튼 Fixed 값
		Integer navScrollY = 0;
		Integer navClientX1 = 657;
		Integer navClientX2 = 969;
		Integer navClientX3 = 1281;
		Integer navClientX4 = 1596;
		Integer navClientY1 = 75;
		Integer navXRange = 35;
		Integer navYRange = 15;

		List<Integer> navClientXList = Arrays.asList(navClientX1, navClientX2, navClientX3, navClientX4);

		// 네비게이션 버튼 클릭 이벤트 생성 [정상 및 비정상 데이터]
		for (int i = 0; i < 500; i++) {
			Integer navClientX = navClientXList.get((i / 5) % navClientXList.size()) + getRandomOffset(navXRange);
			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId + (i % 50),                                                      // 사용자 식별자
				pageUrl,                                                        // 이벤트 페이지 URL
				"click",                                                        // 이벤트 타입: click, scroll, move 등
				navScrollY,                                                     // scrollY
				scrollHeight,                                                   // scrollHeight
				viewportHeight,                                                 // viewportHeight
				browserWidth,                                                   // browserWidth
				timestamp + i,                                                  // timestamp (밀리초 단위)
				navClientX,                                                     // clientX
				navClientY1 + getRandomOffset(navYRange),                       // clientY
				"button",                                                       // element
				serialNumber,                                                   // serialNumber
				i % 25 == 0                                                     // isOutlier
			);
			docs.add(doc);
		}

		// 네비게이션 버튼 이동 및 스크롤 이벤트 생성 [정상 및 비정상 데이터]
		for (int i = 0; i < 2000; i++) {
			int navClientX = navClientXList.get((i / 5) % navClientXList.size()) + getRandomOffset(navXRange) * 5;
			int navClientY = navClientY1 + getRandomOffset(navYRange);
			if (navClientX > 1919) {
				navClientX = 1919;
			}
			if (navClientY < 0) {
				navClientY = 0;
			}
			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId + (i % 50),                                                         // 사용자 식별자
				pageUrl,                                                         // 이벤트 페이지 URL
				(i % 4 == 0) ? "scroll" : "move",                                // 이벤트 타입: click, scroll, move 등
				navScrollY,                                                      // scrollY
				scrollHeight,                                                    // scrollHeight
				viewportHeight,                                                  // viewportHeight
				browserWidth,                                                    // browserWidth
				timestamp + i,                                                   // timestamp (밀리초 단위)
				navClientX,                                                      // clientX
				navClientY,                                                         // clientY
				null,                                                            // element
				serialNumber,                                                    // serialNumber
				i % 10 == 0                                                      // isOutlier
			);
			docs.add(doc);
		}

		// 검색창 Fixed 값
		Integer searchScrollY = 0;
		Integer searchClientX1 = 653;
		Integer searchClientX2 = 965;
		Integer searchClientX3 = 1289;
		Integer searchClientY1 = 615;
		Integer searchXRange = 200;
		Integer searchYRange = 100;

		List<Integer> heroClientXList = Arrays.asList(searchClientX1, searchClientX2, searchClientX3);

		// 검색창 클릭 이벤트 생성 [정상 및 비정상 데이터]
		for (int i = 0; i < 500; i++) {
			Integer heroClientX = heroClientXList.get((i / 3) % heroClientXList.size()) + getRandomOffset(searchXRange);
			Integer heroClientY = searchClientY1 + getRandomOffset(searchYRange);
			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId + (i % 50),                                                       // 사용자 식별자
				serialNumber,                                                    // 이벤트 페이지 URL
				"click",                                                         // 이벤트 타입: click, scroll, move 등
				searchScrollY,                                                     // scrollY
				scrollHeight,                                                    // scrollHeight
				viewportHeight,                                                  // viewportHeight
				browserWidth,                                                    // browserWidth
				timestamp + i,                                                   // timestamp (밀리초 단위)
				heroClientX,                                                     // clientX
				heroClientY,                                                     // clientY
				"div",                                                           // element
				serialNumber,                                                    // serialNumber
				i % 25 == 0                                                      // isOutlier
			);
			docs.add(doc);
		}

		// 검색창 이동 및 스크롤 이벤트 생성 [정상 및 비정상 데이터]
		for (int i = 0; i < 1300; i++) {
			int heroClientX =
				heroClientXList.get((i / 3) % heroClientXList.size()) + getRandomOffset(searchXRange) * 5;
			int heroClientY =
				searchClientY1 + getRandomOffset(searchYRange) * 5;
			if (heroClientY < 0) {
				heroClientY = 0;
			}
			if (heroClientY > 1919) {
				heroClientY = 1919;
			}
			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId + (i % 50),                                                       // 사용자 식별자
				pageUrl,                                                         // 이벤트 페이지 URL
				(i % 4 == 0) ? "scroll" : "move",                             // 이벤트 타입: click, scroll, move 등
				searchScrollY,                                                     // scrollY
				scrollHeight,                                                    // scrollHeight
				viewportHeight,                                                  // viewportHeight
				browserWidth,                                                    // browserWidth
				timestamp + i,                                                   // timestamp (밀리초 단위)
				heroClientX,                                                     // clientX
				heroClientY,                                                     // clientY
				null,                                                            // element
				serialNumber,                                                    // serialNumber
				i % 20 == 0                                                      // isOutlier
			);
			docs.add(doc);
		}

		// 사이트 하단 상품 이미지 Fixed 값
		Integer productScrollY = 0;
		Integer productClientX1 = 263;
		Integer productClientX2 = 615;
		Integer productClientX3 = 986;
		Integer productClientX4 = 1344;
		Integer productClientX5 = 1715;
		Integer productClientY1 = 955;
		Integer productXRange = 65;
		Integer productYRange = 65;

		List<Integer> productClientXList = Arrays.asList(productClientX1, productClientX2, productClientX3,
			productClientX4, productClientX5);

		// 사이트 하단 상품 이미지 클릭 이벤트 생성 [정상 및 비정상 데이터]
		for (int i = 0; i < 500; i++) {
			Integer productClientX =
				productClientXList.get((i / 5) % productClientXList.size()) + getRandomOffset(searchXRange);
			Integer productClientY = searchClientY1 + getRandomOffset(searchYRange);
			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId + (i % 50),                                                       // 사용자 식별자
				serialNumber,                                                    // 이벤트 페이지 URL
				"click",                                                         // 이벤트 타입: click, scroll, move 등
				searchScrollY,                                                     // scrollY
				scrollHeight,                                                    // scrollHeight
				viewportHeight,                                                  // viewportHeight
				browserWidth,                                                    // browserWidth
				timestamp + i,                                                   // timestamp (밀리초 단위)
				productClientX,                                                     // clientX
				productClientY,                                                     // clientY
				"img",                                                           // element
				serialNumber,                                                    // serialNumber
				i % 25 == 0                                                      // isOutlier
			);
			docs.add(doc);
		}

		// 사이트 하단 상품 이미지 이동 및 스크롤 이벤트 생성 [정상 및 비정상 데이터]
		for (int i = 0; i < 2000; i++) {
			int heroClientX =
				heroClientXList.get((5 / 3) % heroClientXList.size()) + getRandomOffset(searchXRange) * 5;
			int heroClientY =
				searchClientY1 + getRandomOffset(searchYRange) * 5;
			if (heroClientY < 0) {
				heroClientY = 0;
			}
			if (heroClientY > 1919) {
				heroClientY = 1919;
			}
			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId + (i % 50),                                                       // 사용자 식별자
				pageUrl,                                                         // 이벤트 페이지 URL
				(i % 4 == 0) ? "scroll" : "move",                             // 이벤트 타입: click, scroll, move 등
				searchScrollY,                                                     // scrollY
				scrollHeight,                                                    // scrollHeight
				viewportHeight,                                                  // viewportHeight
				browserWidth,                                                    // browserWidth
				timestamp + i,                                                   // timestamp (밀리초 단위)
				heroClientX,                                                     // clientX
				heroClientY,                                                     // clientY
				null,                                                            // element
				serialNumber,                                                    // serialNumber
				i % 20 == 0                                                      // isOutlier
			);
			docs.add(doc);
		}

		// 로그인, 회원가입 Fixed 값
		Integer middleScrollY = 0;
		Integer middleClientX1 = 1745;
		Integer middleClientX2 = 1806;
		Integer middleClientY1 = 30;
		Integer middleXRange = 20;
		Integer middleYRange = 10;

		List<Integer> middleClientXList = Arrays.asList(middleClientX1, middleClientX2);

		// 로그인, 회원가입 클릭 이벤트 생성 [정상 및 비정상 데이터]
		for (int i = 0; i < 250; i++) {
			Integer middleClientX =
				middleClientXList.get((i / 2) % middleClientXList.size()) + getRandomOffset(middleXRange);
			Integer middleClientY = middleClientY1 + getRandomOffset(middleYRange);
			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId + (i % 50),                                                       // 사용자 식별자
				pageUrl,                                                         // 이벤트 페이지 URL
				"click",                                                         // 이벤트 타입: click, scroll, move 등
				middleScrollY,                                                   // scrollY
				scrollHeight,                                                    // scrollHeight
				viewportHeight,                                                  // viewportHeight
				browserWidth,                                                    // browserWidth
				timestamp + i,                                                   // timestamp (밀리초 단위)
				middleClientX,                                                   // clientX
				middleClientY,                                                   // clientY
				"button",                                                        // element
				serialNumber,                                                    // serialNumber
				i % 25 == 0                                                      // isOutlier
			);
			docs.add(doc);
		}

		// 로그인, 회원가입 이동 및 스크롤 이벤트 생성 [정상 및 비정상 데이터]
		for (int i = 0; i < 1000; i++) {
			Integer middleClientX =
				middleClientXList.get((i / 2) % middleClientXList.size()) + getRandomOffset(middleXRange) * 5;
			Integer middleClientY = middleClientY1 + getRandomOffset(middleYRange) * 5;
			SolutionEventDocument doc = SolutionEventDocument.create(
				sessionId + (i % 50),                                                       // 사용자 식별자
				pageUrl,                                                         // 이벤트 페이지 URL
				(i % 4 == 0) ? "scroll" : "move",                             // 이벤트 타입: click, scroll, move 등
				middleScrollY,                                                   // scrollY
				scrollHeight,                                                    // scrollHeight
				viewportHeight,                                                  // viewportHeight
				browserWidth,                                                    // browserWidth
				timestamp + i,                                                   // timestamp (밀리초 단위)
				middleClientX,                                                   // clientX
				middleClientY,                                                   // clientY
				null,                                                            // element
				serialNumber,                                                    // serialNumber
				i % 20 == 0                                                      // isOutlier
			);
			docs.add(doc);
		}

		solutionEventDocumentRepository.saveAll(docs);

		log.info("솔루션용 테스트 데이터 등록 완료");
	}

	public void work5() {
		solutionScheduler.processExpiredRegisters();
		log.info("AI 솔루션 결과 생성 완료 (Gemini)");
	}
}
