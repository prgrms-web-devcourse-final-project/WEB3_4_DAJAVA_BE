package com.dajava.backend.domain.solution.utill;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.solution.dto.SolutionRequestDto;
import com.dajava.backend.global.utils.SolutionUtils;

class SolutionUtilTest {
	@Test
	void refinePrompt() {

	}

	@Test
	void testBuildRefineData() {
		String prompt = "테스트 프롬프트";
		String result = SolutionUtils.buildRefineData(prompt);

		assertNotNull(result);
		assertTrue(result.contains(prompt));
	}

}