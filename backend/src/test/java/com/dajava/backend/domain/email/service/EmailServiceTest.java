package com.dajava.backend.domain.email.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.dajava.backend.domain.email.EmailService;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private EmailService emailService;

	@Test
	@DisplayName("1. 서비스 신청 이메일 전송 테스트")
	public void t001() {
		// given
		String to = "solution@example.com";
		String pageUrl = "https://example.com";
		String serialNumber = "5_team_testSerial";

		// when
		emailService.sendRegisterCreateEmail(to, pageUrl, serialNumber);

		// then
		ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender, times(1)).send(messageCaptor.capture());
		SimpleMailMessage sentMessage = messageCaptor.getValue();

		assertThat(sentMessage.getTo()).containsExactly(to);
		assertThat(sentMessage.getSubject()).isEqualTo("DAJAVA 솔루션 신청 정보입니다.");
		assertThat(sentMessage.getText()).contains(pageUrl)
			.contains(serialNumber);
		assertThat(sentMessage.getFrom()).isEqualTo("noreply@dajava.com");
	}

	@Test
	@DisplayName("2. 서비스 완료 이메일 전송 테스트")
	public void t002() {
		// given
		String to = "solution@example.com";
		String pageUrl = "http://solution.com";
		String serialNumber = "5_team_testSerial";

		// when
		emailService.sendSolutionCompleteEmail(to, pageUrl, serialNumber);

		// then
		ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender, times(1)).send(messageCaptor.capture());
		SimpleMailMessage sentMessage = messageCaptor.getValue();

		assertThat(sentMessage.getTo()).containsExactly(to);
		assertThat(sentMessage.getSubject()).isEqualTo("DAJAVA 솔루션 완료 알림입니다.");
		assertThat(sentMessage.getText()).contains(pageUrl)
			.contains(serialNumber);
		assertThat(sentMessage.getFrom()).isEqualTo("noreply@dajava.com");
	}

	@Test
	@DisplayName("3. 이메일 주소가 null 또는 빈 문자열 테스트")
	public void t003() {
		// given
		String to = "";
		String pageUrl = "http://example.com";
		String serialNumber = "123456";

		// when
		emailService.sendRegisterCreateEmail(to, pageUrl, serialNumber);

		// then: 수신자 이메일이 제공되지 않았으므로 메일 전송은 수행되지 않아야 함
		verify(mailSender, never()).send(any(SimpleMailMessage.class));
	}

	@Test
	@DisplayName("4. JavaMailSender 예외 발생 테스트")
	public void t004() {
		// given: JavaMailSender.send()에서 예외 발생을 모의
		String to = "error@example.com";
		String pageUrl = "http://error.com";
		String serialNumber = "0001";

		doThrow(new RuntimeException("SMTP failure"))
			.when(mailSender).send(any(SimpleMailMessage.class));

		// when: 예외가 발생해도 catch 문에서 처리하므로 예외는 전파되지 않아야 함
		emailService.sendSolutionCompleteEmail(to, pageUrl, serialNumber);

		// then: send 메서드가 호출됐음을 검증
		verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
	}
}
