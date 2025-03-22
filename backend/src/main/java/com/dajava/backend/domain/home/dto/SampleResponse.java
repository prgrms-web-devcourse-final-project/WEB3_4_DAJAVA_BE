package com.dajava.backend.domain.home.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record SampleResponse(
	@NonNull
	String content
) { }
