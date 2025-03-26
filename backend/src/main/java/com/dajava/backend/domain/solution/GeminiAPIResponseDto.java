package com.dajava.backend.domain.solution;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class GeminiAPIResponseDto {
	private List<Map<String, Object>> candidates;
	private Map<String, Object> usageMetadata;
	private String modelVersion;
}
