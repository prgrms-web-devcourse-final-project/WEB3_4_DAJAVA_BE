package com.dajava.backend.domain.register;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventState {
	IN_PROGRESS("IN_PROGRESS"),
	REJECTED("REJECTED"),
	COMPLETED("COMPLETED");

	private String state;
}
