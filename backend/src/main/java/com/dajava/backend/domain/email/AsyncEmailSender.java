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
public class AsyncEmailSender {
	private JavaMailSender mailSender;
	private final String username;

	public AsyncEmailSender(JavaMailSender mailSender, @Value("${spring.mail.username}") String username) {
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
}
