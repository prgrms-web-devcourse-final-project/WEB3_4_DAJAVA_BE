package com.dajava.backend.domain.register.service;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.dajava.backend.domain.register.dto.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.RegisterCreateResponse;
import com.dajava.backend.domain.register.dto.RegisterModifyRequest;
import com.dajava.backend.domain.register.dto.RegistersInfoRequest;
import com.dajava.backend.domain.register.dto.RegistersInfoResponse;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;

@ActiveProfiles("test")
@SpringBootTest
class RegisterServiceTest {

	@Autowired
	RegisterService service;

	@Autowired
	RegisterRepository repository;

	@BeforeEach
	void setUp() throws Exception {
		// 혹시 테스트 중에 생성되어있던 것이 있다면 삭제
		repository.deleteAll();
	}

	@Test
	@DisplayName("솔루션 생성")
	public void t1() {
		RegisterCreateRequest request = new RegisterCreateRequest(
			"chsan626@gmail.com",
			"password123!",
			"localhost:3000/test123",
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(7)
		);

		RegisterCreateResponse response = service.createRegister(request);
		Assertions.assertNotNull(response);
		Assertions.assertEquals(1, repository.findAll().size());
	}

	@Test
	@DisplayName("솔루션 수정")
	public void t2() {

		// 솔루션 하나를 미리 생성
		t1();

		Register solution = repository.findAll().get(0);
		Long solutionId = solution.getId();
		RegisterModifyRequest request = new RegisterModifyRequest(
			solution.getEndDate().plusDays(3)
		);
		int curDuration = solution.getDuration();

		service.modifySolution(request, solutionId);
		Register modifiedSolution = repository.findById(solutionId).get();

		Assertions.assertNotNull(modifiedSolution);
		Assertions.assertEquals(curDuration + 72, modifiedSolution.getDuration());
	}

	@Test
	@DisplayName("솔루션 리스트 조회")
	public void t3() {
		for (int i = 0; i < 10; i++) {
			RegisterCreateRequest request = new RegisterCreateRequest(
				"chsan626@gmail.com",
				"password123!",
				"localhost:3000/test123" + i,
				LocalDateTime.now(),
				LocalDateTime.now().plusDays(7)
			);
			service.createRegister(request);
		}

		RegistersInfoRequest request = new RegistersInfoRequest(5, 1);
		RegistersInfoResponse registerList = service.getRegisterList(request);
		Assertions.assertNotNull(registerList);

		Assertions.assertEquals(5, registerList.registerInfos().size());
		;
	}
}
