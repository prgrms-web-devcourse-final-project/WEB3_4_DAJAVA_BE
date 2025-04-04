package com.dajava.backend.global.filter;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.register.service.RegisterCacheService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 엔드포인트 클릭, 이동, 스크롤 요청의 JSON 을 파싱해 memberSerialNumber 를 추출해 확인합니다.
 * @author Metronon
 * @since 2025-03-27
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogRequestFilter implements Filter {

	private final RegisterCacheService registerCacheService;

	public LogRequestFilter(RegisterCacheService registerCacheService) {
		this.registerCacheService = registerCacheService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;

		String path = httpRequest.getRequestURI();

		// 클릭, 이동, 스크롤 관련 로그 요청에 대해서만 필터 적용
		if (path.endsWith("/click")
			|| path.endsWith("/movement")
			|| path.endsWith("/scroll")
		) {
			try {
				// 재사용 가능한 요청 바디를 위한 래퍼 생성
				CachedBodyHttpServletRequest cachedBodyRequest = new CachedBodyHttpServletRequest(httpRequest);

				// 요청 본문 읽어오기
				String requestBody = cachedBodyRequest.getBody();

				// JSON 파싱
				JSONObject jsonObject = new JSONObject(requestBody);

				// memberSerialNumber 추출
				if (jsonObject.has("memberSerialNumber")) {
					String memberSerialNumber = jsonObject.getString("memberSerialNumber");

					// 유효성 검증
					if (!registerCacheService.isValidSerialNumber(memberSerialNumber)) {
						log.warn("유효하지 않은 memberSerialNumber: {}", memberSerialNumber);
						httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						httpResponse.getWriter().write("유효하지 않은 일련번호(member_serial_number) 입니다.");
						return;
					}

					log.debug("유효한 memberSerialNumber: {}", memberSerialNumber);
				} else {
					log.warn("memberSerialNumber가 요청에 없습니다");
					httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					httpResponse.getWriter().write("일련번호(member_serial_number) 가 존재하지 않습니다.");
					return;
				}

				// 캐시된 요청 바디를 사용하는 래퍼를 체인에 전달
				chain.doFilter(cachedBodyRequest, response);
			} catch (Exception e) {
				log.error("로그 요청 처리 중 오류 발생", e);
				httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				httpResponse.getWriter().write("로그 데이터 요청 중 오류가 발생했습니다.");
			}
		}
		// pageCapture API 엔드포인트에 대한 필터
		else if (path.matches("^/v1/register/[^/]+/page-capture$")) {
			int prefixLength = "/v1/register/".length();
			int suffixIndex = path.indexOf("/page-capture");
			String serialNumber = path.substring(prefixLength, suffixIndex);

			if (!registerCacheService.isValidSerialNumber(serialNumber)) {
				log.warn("유효하지 않은 serialNumber: {}", serialNumber);
				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				httpResponse.getWriter().write("유효하지 않은 일련번호 입니다.");
				return;
			}
			log.debug("유효한 serialNumber: {}", serialNumber);
			chain.doFilter(request, response);
		} else {
			// 다른 요청은 필터 통과
			chain.doFilter(request, response);
		}
	}
}
