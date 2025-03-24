package com.dajava.backend.domain.solution;

import com.dajava.backend.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class AISolution extends BaseTimeEntity {
	@Id
	private Long id;

	@Column(nullable = false)
	String serial_number;

	public String getAISolution(String logData) {
		return logData;
	}
}
