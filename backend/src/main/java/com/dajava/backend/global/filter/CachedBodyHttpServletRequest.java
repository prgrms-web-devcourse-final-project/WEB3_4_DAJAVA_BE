package com.dajava.backend.global.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * 요청 바디를 캐시하여 여러 번 읽을 수 있게 하는 HttpServletRequest 래퍼 클래스
 * 필터를 통해 요청 검사시 해당 데이터값이 유실되는 문제를 해결합니다.
 * @author Metronon
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

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
