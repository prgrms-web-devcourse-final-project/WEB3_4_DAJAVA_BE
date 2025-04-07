package com.dajava.backend.global.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
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
import jakarta.servlet.http.Part;
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

		// ✅ preflight 요청은 필터 무시
		if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
			chain.doFilter(request, response);
			return;
		}

		// 요청의 Origin 헤더 가져오기
		String origin = httpRequest.getHeader("Origin");
		if (origin != null) {
			// 요청의 실제 origin으로 CORS 헤더 설정
			httpResponse.setHeader("Access-Control-Allow-Origin", origin);
		} else {
			// Origin 헤더가 없는 경우 기본값 설정 (선택적)
			httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		}
		httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
		httpResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
		httpResponse.setHeader("Access-Control-Allow-Headers", "*");

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
		else if ("/v1/register/page-capture".equals(path)) {
			String serialNumber = null;
			String contentType = httpRequest.getContentType();

			// multipart 요청인 경우, 파트 내에서 "serialNumber" 값을 직접 추출
			if (contentType != null && contentType.toLowerCase().contains("multipart/form-data")) {
				try {
					Collection<Part> parts = httpRequest.getParts();
					for (Part part : parts) {
						if ("serialNumber".equals(part.getName())) {
							try (InputStream is = part.getInputStream()) {
								serialNumber = IOUtils.toString(is, StandardCharsets.UTF_8).trim();
							}
							break;
						}
					}
				} catch (ServletException e) {
					log.error("Multipart 요청의 파트 정보를 가져오는데 실패", e);
					httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					httpResponse.getWriter().write("요청 파싱 중 오류가 발생했습니다.");
					return;
				}
			} else {
				// multipart 가 아닌 요청은 그대로 CachedBodyHttpServletRequest 사용
				CachedBodyHttpServletRequest cachedBodyRequest = new CachedBodyHttpServletRequest(httpRequest);
				serialNumber = cachedBodyRequest.getParameter("serialNumber");
			}

			// 유효성 검사 후 처리
			if (serialNumber == null || !registerCacheService.isValidSerialNumber(serialNumber)) {
				log.warn("유효하지 않은 serialNumber: {}", serialNumber);
				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				httpResponse.getWriter().write("유효하지 않은 일련번호 입니다.");
				return;
			}

			log.debug("유효한 serialNumber: {}", serialNumber);
			chain.doFilter(httpRequest, response);
		} else {
			chain.doFilter(request, response);
		}
	}
}
