package com.dajava.backend.domain.solution;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solutions {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long solutionId;

	String text;

	Long SessionId;

	Long RegisterId;

	LocalDateTime createdAt;
}
