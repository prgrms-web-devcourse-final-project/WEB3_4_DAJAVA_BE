package com.dajava.backend.domain.solution;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SolutionResponseDto {
	private Long id;
	private String text;

	@Builder
	public SolutionResponseDto(Long id, String text) {
		this.id = id;
		this.text = text;
	}

	public static SolutionResponseDto fromEntity(SolutionEntity entity) {
		return SolutionResponseDto.builder()
			.id(entity.getId())
			.text(entity.getText())
			.build();
	}
}
