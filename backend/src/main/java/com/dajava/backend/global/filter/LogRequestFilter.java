package com.dajava.backend.global.filter;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

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
		if (path.startsWith("/log/click") || path.startsWith("/log/movement") || path.startsWith("/log/scroll")) {
			try {
				// 요청 바디를 읽기 위해 래핑함
				ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);

				// 요청 본문 읽어오기
				String requestBody = getRequestBody(requestWrapper);

				// JSON 파싱
				JSONObject jsonObject = new JSONObject(requestBody);

				// memberSerialNumber 추출
				if (jsonObject.has("memberSerialNumber")) {
					String memberSerialNumber = jsonObject.getString("memberSerialNumber");

					// 유효성 검증
					if (!registerCacheService.isValidSerialNumber(memberSerialNumber)) {
						log.warn("유효하지 않은 memberSerialNumber: {}", memberSerialNumber);
						httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						httpResponse.getWriter().write("Invalid serial number");
						return;
					}

					log.debug("유효한 memberSerialNumber: {}", memberSerialNumber);
				} else {
					log.warn("memberSerialNumber가 요청에 없습니다");
					httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					httpResponse.getWriter().write("Missing serial number");
					return;
				}

				chain.doFilter(requestWrapper, response);
			} catch (Exception e) {
				log.error("로그 요청 처리 중 오류 발생", e);
				httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				httpResponse.getWriter().write("Error processing request");
			}
		} else {
			// 다른 요청은 필터 통과
			chain.doFilter(request, response);
		}
	}

	private String getRequestBody(ContentCachingRequestWrapper request) throws IOException {
		String payload = null;
		ContentCachingRequestWrapper wrapper = request;

		// 첫 번째 읽기 시 내용이 캐시되지 않았을 수 있으므로 한 번 읽어둠
		IOUtils.toString(wrapper.getInputStream(), wrapper.getCharacterEncoding());

		if (wrapper.getContentAsByteArray() != null && wrapper.getContentAsByteArray().length > 0) {
			payload = new String(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
		}

		return payload != null ? payload : "";
	}
}
