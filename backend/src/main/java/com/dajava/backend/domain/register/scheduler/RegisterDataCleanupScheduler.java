package com.dajava.backend.domain.register.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.image.service.pageCapture.FileCleanupService;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RegisterDataCleanupScheduler {

	@Value("${cleanup.soft-delete-day.solution}")
	private int cleanupDays;

	private final RegisterRepository registerRepository;
	private final FileCleanupService fileCleanupService;

	public RegisterDataCleanupScheduler(
		RegisterRepository registerRepository,
		FileCleanupService fileCleanupService
	) {
		this.registerRepository = registerRepository;
		this.fileCleanupService = fileCleanupService;
	}

	/**
	 * 1일 (86400000ms) 간격으로 실행되는 스케줄러
	 * Register 의 modifiedDate가 (현재 시각 - softDeleteDays)보다 이전인 경우,
	 * 해당 Register 에 연결된 PageCaptureData, Solution 및 이미지를 삭제합니다.
	 */
	@Scheduled(fixedDelayString = "${cleanup.scheduler.duration.solution}")
	@Transactional
	public void cleanupRegisterData() {
		LocalDateTime threshold = LocalDateTime.now().minusDays(cleanupDays);
		List<Register> outdatedRegisters = registerRepository.findAllCompletedRegisterList(threshold);

		log.info("서비스 제공 기간이 지난 Register {}건을 처리합니다.", outdatedRegisters.size());

		for (Register register : outdatedRegisters) {
			// captureData 를 가져와 이미지 파일을 삭제후 데이터 clear
			if (register.getCaptureData() != null) {
				register.getCaptureData().forEach(pageCaptureData -> {
					String fileName = pageCaptureData.getCaptureFileName();
					try {
						fileCleanupService.deleteFile(fileName);
						register.getCaptureData().clear();
						log.info("파일 삭제 성공: {}", fileName);
					} catch (Exception e) {
						log.error("파일 삭제 실패: {}", fileName, e);
					}
				});
			}

			// AI solution 데이터를 삭제 후 고아 객체 삭제
			if (register.getSolution() != null) {
				register.deleteSolution();
			}
			registerRepository.save(register);
			log.info("Register({}) captureData 클리어 후 저장 완료", register.getSerialNumber());
		}
	}
}
