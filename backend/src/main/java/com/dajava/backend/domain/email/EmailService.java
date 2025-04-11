package com.dajava.backend.domain.email;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

	private final AsyncEmailSender asyncEmailSender;

	public EmailService(AsyncEmailSender asyncEmailSender) {
		this.asyncEmailSender = asyncEmailSender;
	}

	public void sendRegisterCreateEmail(String to, String pageUrl, String serialNumber) {
		if (to == null || to.isBlank()) {
			log.info("수신자 이메일 주소가 제공되지 않았습니다.");
			return;
		}

		String subject = "DAJAVA 솔루션 신청 정보입니다.";
		String text = String.format("""
			귀하가 신청하신 서비스 내용은 다음과 같습니다.
			대상 URL = %s
			조회 일련번호 = %s
			""", pageUrl, serialNumber);

		try {
			asyncEmailSender.sendEmail(to, subject, text);
		} catch (Exception e) {
			log.info("서비스 신청 이메일 전송 실패: {}", e.getMessage());
		}
	}

	public void sendSolutionCompleteEmail(String to, String pageUrl, String serialNumber) {
		String subject = "DAJAVA 솔루션 완료 알림입니다.";
		String text = String.format("""
			귀하가 신청하신 서비스의 수집 기간 및 솔루션 데이터 생성이 완료되었습니다.
			대상 URL = %s
			조회 일련번호 = %s
			""", pageUrl, serialNumber);

		try {
			asyncEmailSender.sendEmail(to, subject, text);
		} catch (Exception e) {
			log.info("서비스 완료 이메일 전송 실패: {}", e.getMessage());
		}
	}
}
