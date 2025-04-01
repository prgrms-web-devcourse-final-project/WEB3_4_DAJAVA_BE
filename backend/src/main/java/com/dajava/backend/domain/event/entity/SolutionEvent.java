package com.dajava.backend.domain.event.entity;

import java.time.LocalDateTime;

import com.dajava.backend.global.common.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SolutionEvent extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String sessionId;

	@NotNull
	private String pageUrl;

	@NotNull
	private String type; // click, move, scroll 등

	@NotNull
	private Integer scrollY;

	@NotNull
	private Integer scrollHeight;

	@NotNull
	private Integer viewportHeight;

	@NotNull
	private Integer browserWidth;

	@NotNull
	private LocalDateTime timestamp;

	private Integer clientX;
	private Integer clientY;
	private String element;

	@ManyToOne()
	@JoinColumn(name = "solution_data_id")
	private SolutionData solutionData;

}
