package com.dajava.backend.global.initdata;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.PointerClickEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerMoveEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerScrollEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.register.RegisterCreateResponse;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.register.service.RegisterService;
import com.dajava.backend.global.utils.PasswordUtils;
import com.dajava.backend.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitData {

	@Value("${init.flag:0}")
	private int initFlag;

	private final PointerClickEventDocumentRepository pointerClickEventDocumentRepository;
	private final PointerMoveEventDocumentRepository pointerMoveEventDocumentRepository;
 	private final PointerScrollEventDocumentRepository pointerScrollEventDocumentRepository;
	private final RegisterRepository registerRepository;
	private final SessionDataDocumentRepository sessionDataDocumentRepository;

	private PointerClickEventDocument createEvent(Long time, int clientX, int clientY, String tag) {
		return PointerClickEventDocument.builder()
			.id(UUID.randomUUID().toString())
			.timestamp(time)
			.clientX(clientX)
			.clientY(clientY)
			.sessionId("AiSolutionTestSessionNumber")
			.pageUrl("localhost:3000/test123")
			.browserWidth(1920)
			.memberSerialNumber("AiSolutionTestSerialNumber")
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
			.pageUrl("localhost:3000/test123")
			.browserWidth(1920)
			.memberSerialNumber("AiSolutionTestSerialNumber")
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
			.pageUrl("localhost:3000/test123")
			.sessionId("AiSolutionTestSessionNumber")
			.memberSerialNumber("AiSolutionTestSerialNumber")
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
			self.work1();
			self.work2();
		};
	}

	@Transactional
	public void work1() {

		if (initFlag != 1) {
			return;
		}
		RegisterCreateRequest request = new RegisterCreateRequest(
			"chsan626@gmail.com",
			"password123!",
			"localhost:3000/test123",
			LocalDateTime.now().withHour(0).withMinute(0).withSecond(1).withNano(0).plusDays(0),
			LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0)
		);

		Register register = Register.builder()
			.serialNumber("AiSolutionTestSerialNumber")
			.email(request.email())
			.password(PasswordUtils.hashPassword(request.password()))
			.url(request.url())
			.startDate(request.startDate())
			.endDate(request.endDate())
			.duration(TimeUtils.getDuration(request.startDate(), request.endDate()))
			.isServiceExpired(false)
			.isSolutionComplete(false)
			.pageCapture("")
			.build();

		Register newRegister = registerRepository.save(register);
		log.info("baseInit register 등록 완료");
	}

	public void work2() {

		if (initFlag != 1) {
			return;
		}

		LocalDateTime now = LocalDateTime.now();
		long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		SessionDataDocument sessionData = SessionDataDocument.create(
			"AiSolutionTestSessionNumber",              // sessionId
			"AiSolutionTestSerialNumber",                                 // memberSerialNumber
			"localhost:3000/test123",                      // pageUrl
			timestamp                                       // timestamp (2025-04-07T12:00:00Z 기준 millis)
		);

		sessionDataDocumentRepository.save(sessionData);

		List<PointerClickEventDocument> clickEvents = List.of(
			createEvent(timestamp, 100, 100, "button"),
			createEvent(timestamp + 1, 102, 101, "button"),
			createEvent(timestamp + 2 , 103, 99, "button"),
			createEvent(timestamp, 100, 200, "div"),
			createEvent(timestamp, 200, 100, "button"),
			createEvent(timestamp + 6000, 102, 101, "button"),
			createEvent(timestamp + 12000, 103, 99, "button")
		);

		pointerClickEventDocumentRepository.saveAll(clickEvents);

		List<PointerMoveEventDocument> moveEvents = List.of(
			createMoveEvent(timestamp, 100, 100),        // 시작
			createMoveEvent(timestamp + 100, 110, 100), // →
			createMoveEvent(timestamp + 200, 110, 110), // ↓
			createMoveEvent(timestamp + 300, 100, 110), // ←
			createMoveEvent(timestamp + 400, 100, 100), // ↑
			createMoveEvent(timestamp + 500, 120, 100),  // →
			createMoveEvent(timestamp + 1000, 100, 100),
			createMoveEvent(timestamp + 1100, 110, 100),
			createMoveEvent(timestamp + 1200, 110, 110),
			createMoveEvent(timestamp + 1300, 100, 100),
			createMoveEvent(timestamp + 5000, 110, 100), // 5초 후 → time window 밖
			createMoveEvent(timestamp + 6000, 110, 110),
			createMoveEvent(timestamp + 7000, 100, 110),
			createMoveEvent(timestamp + 8000, 100, 100)
		);

		pointerMoveEventDocumentRepository.saveAll(moveEvents);

		List<PointerScrollEventDocument> scrollEvents = List.of(
			createScrollEvent(timestamp, 100, 2000, 600),
			createScrollEvent(timestamp + 100, 150, 2000, 600),
			createScrollEvent(timestamp + 200, 100, 2000, 600),
			createScrollEvent(timestamp + 300, 160, 2000, 600),
			createScrollEvent(timestamp + 400, 110, 2000, 600),
			createScrollEvent(timestamp, 1000, 3000, 600),
			createScrollEvent(timestamp + 1100, 300, 3000, 600),
			createScrollEvent(timestamp + 1200, 500, 3000, 600),
			createScrollEvent(timestamp, 100, 2000, 600),
			createScrollEvent(timestamp + 5000, 120, 2000, 600), // 시간 초과
			createScrollEvent(timestamp + 10000, 1400, 2000, 600)
		);

		pointerScrollEventDocumentRepository.saveAll(scrollEvents);

		log.info("baseInit pointerEventDocument, sessionDataDocument 등록 완료");
	}




}
