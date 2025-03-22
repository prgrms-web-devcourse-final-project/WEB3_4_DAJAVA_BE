package com.dajava.backend.domain.home;

import com.dajava.backend.global.common.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class Sample extends BaseTimeEntity {

	@Id @GeneratedValue
	private long id;

	private String content;

	public static Sample create(String content) {
		return Sample.builder()
			.content(content)
			.build();
	}
}
