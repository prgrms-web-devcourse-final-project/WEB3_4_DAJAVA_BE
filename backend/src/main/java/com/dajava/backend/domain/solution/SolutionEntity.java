package com.dajava.backend.domain.solution;
import com.dajava.backend.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "AISolution")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolutionEntity extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(columnDefinition = "TEXT",name = "text", nullable = false)
	private String text;
	@Column(name = "sessionId", nullable = false)
	private String sessionId;
	@Column(name = "registerId", nullable = false)
	private String registerId;
}
