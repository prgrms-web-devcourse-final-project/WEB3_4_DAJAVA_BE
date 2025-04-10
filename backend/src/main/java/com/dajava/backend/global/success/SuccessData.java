package com.dajava.backend.global.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessData<T> {
	private String status;  // 성공 상태 (ex: "success")
	private T data;         // 실제 데이터 (필요한 경우)
	private String message; // 메시지 (필요한 경우)

	// SuccessData 생성 메서드
	public static <T> SuccessData<T> create(T data, String message) {
		return new SuccessData<>("success", data, message);
	}
}