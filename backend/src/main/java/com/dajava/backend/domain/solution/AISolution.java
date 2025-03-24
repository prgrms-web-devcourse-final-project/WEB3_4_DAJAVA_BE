package com.dajava.backend.domain.solution;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class AISolution {
	@Id
	private Long id;

	@Column(nullable = false)
	String serial_number;
}
