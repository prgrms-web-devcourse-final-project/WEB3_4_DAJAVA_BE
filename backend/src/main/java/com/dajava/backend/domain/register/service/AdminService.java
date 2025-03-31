package com.dajava.backend.domain.register.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dajava.backend.domain.register.exception.AdminException;
import com.dajava.backend.global.exception.ErrorCode;
import com.dajava.backend.global.utils.PasswordUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminService {

	@Value("${custom.adminCode}")
	private String adminCode;

	public void login(final String adminCode, HttpServletResponse response) {
		if (!adminCode.equals(this.adminCode)) {
			log.info("adminCode : {}, this.adminCode : {}", adminCode, this.adminCode);
			throw new AdminException(ErrorCode.INVALID_ADMIN_CODE);
		}

		// 관리자 코드 대신 "true" 등의 값 사용
		Cookie authCookie = new Cookie("admin_auth", PasswordUtils.hashPassword(adminCode));
		authCookie.setMaxAge(30 * 60);
		authCookie.setHttpOnly(true);
		authCookie.setPath("/");
		response.addCookie(authCookie);
	}

	// authorize 메서드
	public void authorize(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		boolean isAuthenticated = false;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("admin_auth".equals(cookie.getName()) &&
					PasswordUtils.verifyPassword(adminCode, cookie.getValue())) {
					isAuthenticated = true;
					break;
				}
			}
		}

		if (!isAuthenticated) {
			throw new AdminException(ErrorCode.AUTHORIZE_ERROR);
		}
	}
}
