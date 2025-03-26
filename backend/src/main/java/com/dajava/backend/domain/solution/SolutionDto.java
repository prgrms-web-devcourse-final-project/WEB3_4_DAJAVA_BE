package com.dajava.backend.domain.solution;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SolutionDto {
	private Long id;
	private String text;

	@Builder
	public SolutionDto(Long id, String text) {
		this.id = id;
		this.text = text;
	}

	public static SolutionDto fromEntity(SolutionEntity entity) {
		return SolutionDto.builder()
			.id(entity.getId())
			.text(entity.getText())
			.build();
	}
}
