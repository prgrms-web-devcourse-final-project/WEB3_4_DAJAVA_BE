package com.dajava.backend.domain.register.controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.image.service.pageCapture.FileStorageService;
import com.dajava.backend.domain.register.dto.pageCapture.PageCaptureRequest;
import com.dajava.backend.domain.register.dto.pageCapture.PageCaptureResponse;
import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.register.RegisterCreateResponse;
import com.dajava.backend.domain.register.dto.register.RegisterDeleteResponse;
import com.dajava.backend.domain.register.dto.register.RegisterModifyRequest;
import com.dajava.backend.domain.register.dto.register.RegisterModifyResponse;
import com.dajava.backend.domain.register.dto.register.RegistersInfoRequest;
import com.dajava.backend.domain.register.dto.register.RegistersInfoResponse;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.exception.RegisterException;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.register.service.AdminService;
import com.dajava.backend.domain.register.service.RegisterCacheService;
import com.dajava.backend.domain.register.service.RegisterService;
import com.dajava.backend.global.exception.ErrorCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RegisterController
 * "/v1/register" 로 들어오는 신청 관련 API 컨트롤러
 * 신청관련 요청을 처리
 *
 * @author ChoiHyunSan
 * @since 2025-03-24
 */
@Slf4j
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "RegisterController", description = "API 신청 폼 컨트롤러")
@RestController
@RequiredArgsConstructor
public class RegisterController {

	private final RegisterService registerService;
	private final AdminService adminService;
	private final FileStorageService fileStorageService;
	private final RegisterRepository registerRepository;

	/**
	 * 솔루션 신청 폼 접수 API
	 * @param request 신청 데이터 (RegisterCreateRequest)
	 * @return 신청 결과 (RegisterCreateResponse)
	 */
	@Operation(
		summary = "솔루션 등록 요청",
		description = "솔루션 폼 정보를 기반으로 등록 후 일련 번호 등 등록 정보를 반환합니다.")
	@PostMapping("/v1/register")
	@ResponseStatus(HttpStatus.OK)
	public RegisterCreateResponse create(
		@RequestBody RegisterCreateRequest request
	) {
		log.info(request.toString());
		return registerService.createRegister(request);
	}

	/**
	 * 솔루션 수정 요청 API
	 * @param request 수정 데이터 (SolutionModifyRequest)
	 * @return 수정 결과 (SolutionModifyResponse)
	 */
	@Operation(
		summary = "솔루션 수정 요청",
		description = "솔루션 요청 정보를 수정합니다.")
	@PatchMapping("/v1/register/{solutionId}")
	@ResponseStatus(HttpStatus.OK)
	public RegisterModifyResponse modify(
		@RequestBody RegisterModifyRequest request,
		@PathVariable Long solutionId,
		HttpServletRequest httpRequest
	) {
		adminService.authorize(httpRequest);
		return registerService.modifySolution(request, solutionId);
	}

	/**
	 * 솔루션 삭제 요청 API
	 * @return 삭제 결과 (SolutionDeleteResponse)
	 */
	@Operation(
		summary = "솔루션 삭제 요청",
		description = "특정 솔루션을 삭제요청합니다.")
	@DeleteMapping("/v1/register/{solutionId}")
	@ResponseStatus(HttpStatus.OK)
	public RegisterDeleteResponse modify(
		@PathVariable Long solutionId,
		HttpServletRequest httpRequest
	) {
		adminService.authorize(httpRequest);
		return registerService.deleteSolution(solutionId);
	}

	/**
	 * 솔루션 리스트 조회 요청 API
	 * @return 솔루션 리스트 (RegistersInfoResponse)
	 */
	@Operation(
		summary = "솔루션 리스트 조회 요청",
		description = "페이징된 솔루션의 리스트를 반환합니다.")
	@GetMapping("/v1/registers")
	@ResponseStatus(HttpStatus.OK)
	public RegistersInfoResponse list(
		@ModelAttribute RegistersInfoRequest request,
		HttpServletRequest httpRequest
	) {
		adminService.authorize(httpRequest);
		return registerService.getRegisterList(request);
	}

	/**
	 * 관리자 코드 입력 API
	 * 접속에 성공하면 인증 토큰을 쿠키에 담아서 전달
	 */
	@Operation(
		summary = "솔루션 관리자 코드 입력",
		description = "관리자 페이지에 접속하기 위해 관리코드를 입력합니다.")
	@PostMapping("/v1/register/admin")
	@ResponseStatus(HttpStatus.OK)
	public void admin(
		@RequestParam String adminCode,
		HttpServletResponse response
	) {
		adminService.login(adminCode, response);
	}

	/**
	 * 일련번호로 Register 값을 가져오는 로직입니다.
	 * @param serialNumber 고유 일련번호 입니다
	 * @return Register
	 */
	public Register findRegisterBySerialNumber(String serialNumber) {
		return registerRepository.findBySerialNumber(serialNumber)
			.orElseThrow(() -> new RegisterException(ErrorCode.REGISTER_NOT_FOUND));
	}

	/**
	 * 캡쳐 데이터 POST API
	 * 사용자가 세션 생성시 캡쳐하게 되는 페이지 캡쳐 데이터(멀티파트 파일)를 존재하지 않는 경우, 로컬에 저장합니다.
	 * 이후 반환된 로컬 이미지 경로를 register 의 pageCapture 에 POST 합니다.
	 *
	 * @param request serialNumber, pageUrl, imagefile 을 가진 요청 DTO 입니다.
	 * @return PageCaptureResponse 성공 여부, 메시지, 저장된 파일 경로를 반환합니다.
	 */
	@Operation(
		summary = "솔루션 전체 페이지 캡쳐 데이터 삽입",
		description = "멀티파트 파일로 전송된 이미지를 저장하고, pageCapture 컬럼에 이미지 접근 경로를 삽입합니다.")
	@PostMapping(value = "/v1/register/page-capture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public PageCaptureResponse updatePageCapture(
		@ModelAttribute PageCaptureRequest request
	) {
		return registerService.createPageCapture(request);
	}

	private final RegisterCacheService registerCacheService;


	@Operation(
		summary = "솔루션 시리얼넘버의 캐시 정보를 반환하는 테스트 API"
	)
	@GetMapping(value = "/v1/register/test/cacheList")
	@ResponseStatus(HttpStatus.OK)
	public Set<String> getCacheList() {
		return registerCacheService.getSerialNumberCache();
	}

	@Operation(
		summary = "솔루션 시리얼넘버의 캐시 정보를 반환하는 테스트 API"
	)
	@GetMapping(value = "/v1/register/test/expire/{serialNumber}")
	@ResponseStatus(HttpStatus.OK)
	public void expireRegister(
		@PathVariable String serialNumber
	) {
		registerService.expireRegister(serialNumber);
	}
}
