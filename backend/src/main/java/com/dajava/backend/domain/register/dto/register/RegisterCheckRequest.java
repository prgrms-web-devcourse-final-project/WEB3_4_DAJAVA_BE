package com.dajava.backend.domain.register.dto.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
/**
 * <p>{@code SolutiionCheckRequest}는 시리얼 넘버와 표현하는 DTO입니다.</p>
 *
 */
public record RegisterCheckRequest(
	@NotBlank(message = "시리얼 번호는 필수입니다.") String serialNumber,
	@NotNull(message = "비밀번호는 필수입니다.") String password
) {

}