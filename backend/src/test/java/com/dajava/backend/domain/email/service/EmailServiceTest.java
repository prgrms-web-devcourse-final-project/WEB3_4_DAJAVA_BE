package com.dajava.backend.domain.email.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import com.dajava.backend.domain.email.EmailService;

import jakarta.mail.Address;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

	@Mock
	private JavaMailSender mailSender;

	private EmailService emailService;

	// 테스트용으로 username을 "noreply@dajava.com"으로 설정
	private final String username = "noreply@dajava.com";

	@BeforeEach
	public void setUp() throws Exception {
		emailService = new EmailService(mailSender, username);
		lenient().when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session)null));
	}

	@Test
	@DisplayName("1. 서비스 신청 이메일 전송 테스트")
	public void t001() throws Exception {
		// given
		String to = "bumpercar00@gmail.com";
		String pageUrl = "https://example.com";
		String serialNumber = "5_team_testSerial";

		// when
		emailService.sendRegisterCreateEmail(to, pageUrl, serialNumber);

		// then - mailSender.send() 호출 시 전달된 MimeMessage 캡처
		ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
		verify(mailSender, times(1)).send(messageCaptor.capture());
		MimeMessage sentMessage = messageCaptor.getValue();

		// 제목 검증
		assertThat(sentMessage.getSubject()).isEqualTo("DAJAVA 솔루션 신청 정보입니다.");

		// 수신자 검증
		Address[] toAddresses = sentMessage.getRecipients(MimeMessage.RecipientType.TO);
		assertThat(toAddresses).isNotNull();
		assertThat(toAddresses).hasSize(1);
		assertThat(((InternetAddress)toAddresses[0]).getAddress()).isEqualTo(to);

		// 본문 검증: getContent()로 텍스트 내용 조회
		Object content = sentMessage.getContent();
		assertThat(content.toString()).contains(pageUrl)
			.contains(serialNumber);

		// 발신자(From) 검증
		Address[] fromAddresses = sentMessage.getFrom();
		assertThat(fromAddresses).isNotNull();
		assertThat(fromAddresses).hasSize(1);
		InternetAddress from = (InternetAddress)fromAddresses[0];
		assertThat(from.getAddress()).isEqualTo(username);
	}

	@Test
	@DisplayName("2. 서비스 완료 이메일 전송 테스트")
	public void t002() throws Exception {
		// given
		String to = "bumpercar00@gmail.com";
		String pageUrl = "http://solution.com";
		String serialNumber = "5_team_testSerial";

		// when
		emailService.sendSolutionCompleteEmail(to, pageUrl, serialNumber);

		// then
		ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
		verify(mailSender, times(1)).send(messageCaptor.capture());
		MimeMessage sentMessage = messageCaptor.getValue();

		// 제목 검증
		assertThat(sentMessage.getSubject()).isEqualTo("DAJAVA 솔루션 완료 알림입니다.");

		// 수신자 검증
		Address[] toAddresses = sentMessage.getRecipients(MimeMessage.RecipientType.TO);
		assertThat(toAddresses).isNotNull();
		assertThat(toAddresses).hasSize(1);
		assertThat(((InternetAddress)toAddresses[0]).getAddress()).isEqualTo(to);

		// 본문 검증
		Object content = sentMessage.getContent();
		assertThat(content.toString()).contains(pageUrl)
			.contains(serialNumber);

		// 발신자(From) 검증
		Address[] fromAddresses = sentMessage.getFrom();
		assertThat(fromAddresses).isNotNull();
		assertThat(fromAddresses).hasSize(1);
		InternetAddress from = (InternetAddress)fromAddresses[0];
		assertThat(from.getAddress()).isEqualTo(username);
	}

	@Test
	@DisplayName("3. 이메일 주소가 null 또는 빈 문자열 테스트")
	public void t003() throws Exception {
		// given
		String to = "";
		String pageUrl = "http://example.com";
		String serialNumber = "123456";

		// when
		emailService.sendRegisterCreateEmail(to, pageUrl, serialNumber);

		// then: 수신자 이메일이 없으므로 send() 메서드가 호출되지 않아야 함
		verify(mailSender, never()).send(any(MimeMessage.class));
	}

	@Test
	@DisplayName("4. JavaMailSender 예외 발생 테스트")
	public void t004() throws Exception {
		// given: mailSender.send()에서 예외를 발생하도록 모의
		String to = "error@example.com";
		String pageUrl = "http://error.com";
		String serialNumber = "0001";

		doThrow(new RuntimeException("SMTP failure"))
			.when(mailSender).send(any(MimeMessage.class));

		// when: 예외가 발생해도 catch문에서 처리되므로 예외는 전파되지 않아야 함
		emailService.sendSolutionCompleteEmail(to, pageUrl, serialNumber);

		// then: send() 메서드가 1회 호출된 것을 검증
		verify(mailSender, times(1)).send(any(MimeMessage.class));
	}
}

