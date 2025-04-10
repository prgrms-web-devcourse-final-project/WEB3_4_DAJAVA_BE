package com.dajava.backend.global.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessData<T> {
	private String status;
	private T data;
	private String message;

	public static <T> SuccessData<T> create(T data, String message) {
		return new SuccessData<>("success", data, message);
	}
}
