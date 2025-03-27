package com.dajava.backend.domain.event;

import java.time.LocalDateTime;

import com.dajava.backend.global.common.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class PointerEvent extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String sessionId;

	@NotNull
	private String pageUrl;

	@NotNull
	private String type; // click, move, scroll 등

	private Integer clientX;
	private Integer clientY;
	private Integer scrollY;
	private Integer scrollHeight;
	private Integer viewportHeight;
	private String element;

	@NotNull
	private LocalDateTime timestamp;

	@NotNull
	private Integer browserWidth;

	@ManyToOne()
	@JoinColumn(name = "solution_data_id")
	private SolutionData solutionData;

}
