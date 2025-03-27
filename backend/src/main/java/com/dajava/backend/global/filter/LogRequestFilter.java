package com.dajava.backend.global.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.register.service.RegisterCacheService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
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

				// 캐시된 요청 바디를 사용하는 래퍼를 체인에 전달
				chain.doFilter(cachedBodyRequest, response);
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
}

/**
 * 요청 바디를 캐시하여 여러 번 읽을 수 있게 하는 HttpServletRequest 래퍼 클래스
 */
class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

	private final byte[] cachedBody;
	private final String cachedBodyString;

	public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
		super(request);

		// 요청 바디를 바이트 배열로 읽어옴
		InputStream requestInputStream = request.getInputStream();
		this.cachedBody = IOUtils.toByteArray(requestInputStream);

		// 인코딩이 null인 경우 기본값 사용
		String encoding = request.getCharacterEncoding();
		if (encoding == null) {
			encoding = "UTF-8"; // 기본 인코딩으로 UTF-8 사용
		}

		this.cachedBodyString = new String(this.cachedBody, encoding);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		// 캐시된 바디를 기반으로 새 InputStream 반환
		return new CachedServletInputStream(this.cachedBody);
	}

	@Override
	public BufferedReader getReader() throws IOException {
		// 캐시된 바디를 기반으로 새 Reader 반환
		String encoding = getCharacterEncoding();
		if (encoding == null) {
			encoding = "UTF-8";
		}
		return new BufferedReader(new InputStreamReader(getInputStream(), encoding));
	}

	public String getBody() {
		return this.cachedBodyString;
	}

	/**
	 * 바이트 배열을 기반으로 하는 ServletInputStream 구현
	 */
	private static class CachedServletInputStream extends ServletInputStream {

		private final ByteArrayInputStream buffer;

		public CachedServletInputStream(byte[] contents) {
			this.buffer = new ByteArrayInputStream(contents);
		}

		@Override
		public int read() throws IOException {
			return buffer.read();
		}

		@Override
		public boolean isFinished() {
			return buffer.available() == 0;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener listener) {
			throw new UnsupportedOperationException("ReadListener not supported");
		}
	}
}
