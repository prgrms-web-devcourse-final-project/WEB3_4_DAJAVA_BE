package com.dajava.backend.domain.home.dto;

import lombok.NonNull;

public record SampleRequest(
	@NonNull
	String content
) { }
