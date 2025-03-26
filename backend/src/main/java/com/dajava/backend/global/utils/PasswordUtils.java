package com.dajava.backend.global.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {

	/**
	 * 비밀번호를 해싱합니다 (솔트 적용)
	 * @param password 원본 비밀번호
	 * @return 솔트와 해시가 결합된 문자열 (format: salt:hash)
	 */
	public static String hashPassword(String password) {
		try {
			// 솔트 생성
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[16];
			random.nextBytes(salt);

			// 비밀번호 해싱
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

			// Base64 인코딩
			String saltStr = Base64.getEncoder().encodeToString(salt);
			String hashStr = Base64.getEncoder().encodeToString(hashedPassword);

			// 솔트와 해시 결합
			return saltStr + ":" + hashStr;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("비밀번호 해싱 오류", e);
		}
	}

	/**
	 * 비밀번호 검증
	 * @param inputPassword 검증할 비밀번호
	 * @param storedHash 저장된 해시 (format: salt:hash)
	 * @return 일치 여부
	 */
	public static boolean verifyPassword(String inputPassword, String storedHash) {
		try {
			// 저장된 해시에서 솔트와 해시 분리
			String[] parts = storedHash.split(":");
			if (parts.length != 2) {
				return false;
			}

			byte[] salt = Base64.getDecoder().decode(parts[0]);
			byte[] hash = Base64.getDecoder().decode(parts[1]);

			// 입력 비밀번호 해싱
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			byte[] inputHash = md.digest(inputPassword.getBytes(StandardCharsets.UTF_8));

			// 해시 비교
			return MessageDigest.isEqual(hash, inputHash);
		} catch (Exception e) {
			return false;
		}
	}
}
