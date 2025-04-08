package com.dajava.backend.domain.register.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PageCaptureData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String pageCapturePath;

	@Column(nullable = false)
	private String pageUrl;

	@ManyToOne
	@JoinColumn(name = "register_id", nullable = false)
	private Register register;

	public void updatePageCapturePath(String pageCapturePath) {
		this.pageCapturePath = pageCapturePath;
	}
}
