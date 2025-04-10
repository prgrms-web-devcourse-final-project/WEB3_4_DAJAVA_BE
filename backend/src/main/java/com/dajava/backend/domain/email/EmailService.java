package com.dajava.backend.domain.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

	private final JavaMailSender mailSender;
	private final String username;

	public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String username) {
		this.mailSender = mailSender;
		this.username = username;
	}

	@Async
	public void sendEmail(String to, String subject, String text) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text);
			helper.setFrom(new InternetAddress(username, "DAJAVA no-reply"));

			mailSender.send(message);
			log.info("이메일 전송 완료: {}", to);
		} catch (Exception e) {
			log.error("이메일 전송 실패: {}", e.getMessage(), e);
		}
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
			sendEmail(to, subject, text);
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
			sendEmail(to, subject, text);
		} catch (Exception e) {
			log.info("서비스 완료 이메일 전송 실패: {}", e.getMessage());
		}
	}
}
