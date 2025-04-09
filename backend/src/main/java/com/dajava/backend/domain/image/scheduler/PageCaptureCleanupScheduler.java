package com.dajava.backend.domain.image.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.image.service.pageCapture.FileStorageService;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PageCaptureCleanupScheduler {

	private final RegisterRepository registerRepository;
	private final FileStorageService fileStorageService;

	// application-secret.yml 또는 다른 설정 파일에 정의된 값 주입
	@Value("${cleanup.soft-delete-day.solution}")
	private int cleanupDays;

	public PageCaptureCleanupScheduler(
		RegisterRepository registerRepository,
		FileStorageService fileStorageService
	) {
		this.registerRepository = registerRepository;
		this.fileStorageService = fileStorageService;
	}

	/**
	 * 1일 (86400000ms) 간격으로 실행되는 스케줄러
	 * Register 의 modifiedDate가 (현재 시각 - softDeleteDays)보다 이전인 경우,
	 * 해당 Register 에 연결된 PageCaptureData 의 파일들을 삭제한 후, Register 를 삭제합니다.
	 *
	 */
	@Scheduled(fixedDelayString = "${cleanup.scheduler.duration.solution}")
	public void cleanupSoftDeletedRegisters() {
		LocalDateTime threshold = LocalDateTime.now().minusDays(cleanupDays);
		List<Register> outdatedRegisters = registerRepository.findAllCompletedRegisterList(threshold);

		log.info("서비스 제공 기간이 지난 Register {}건을 처리합니다.", outdatedRegisters.size());

		for (Register register : outdatedRegisters) {
			if (register.getCaptureData() != null) {
				register.getCaptureData().forEach(pageCaptureData -> {
					String fileName = pageCaptureData.getCaptureFileName();
					try {
						fileStorageService.deleteFile(fileName);
						log.info("파일 삭제 성공: {}", fileName);
					} catch (Exception e) {
						log.error("파일 삭제 실패: {}", fileName, e);
					}
				});
			}
			register.getCaptureData().clear();
			registerRepository.save(register);
			log.info("Register({}) captureData 클리어 후 저장 완료", register.getSerialNumber());
		}
	}
}
