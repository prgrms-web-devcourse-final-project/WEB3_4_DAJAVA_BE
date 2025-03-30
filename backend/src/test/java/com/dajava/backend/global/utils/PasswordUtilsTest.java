package com.dajava.backend.global.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordUtilsTest {

	@Test
	@DisplayName("해싱 및 검증이 제대로 이루어짐")
	void t1() {
		String password = "SamplePassword";
		String hashPassword = PasswordUtils.hashPassword(password);

		Assertions.assertTrue(PasswordUtils.verifyPassword(password, hashPassword));
	}
}
