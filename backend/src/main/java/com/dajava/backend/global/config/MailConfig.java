package com.dajava.backend.global.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

	@Bean
	public JavaMailSender javaMailSender(
		@Value("${spring.mail.host}") String host,
		@Value("${spring.mail.port}") int port,
		@Value("${spring.mail.username}") String username,
		@Value("${spring.mail.password}") String password,
		@Value("${spring.mail.properties.mail.smtp.auth}") boolean auth,
		@Value("${spring.mail.properties.mail.smtp.starttls.enable}") boolean starttls,
		@Value("${spring.mail.properties.mail.smtp.timeout}") int timeout) {

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setUsername(username);
		mailSender.setPassword(password);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.smtp.auth", auth);
		props.put("mail.smtp.starttls.enable", starttls);
		props.put("mail.smtp.timeout", timeout);

		return mailSender;
	}
}
