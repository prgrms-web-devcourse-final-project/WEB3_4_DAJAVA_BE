package com.dajava.backend.domain.heatmap.exception;

import com.dajava.backend.global.exception.ErrorCode;

public class HeatmapException extends RuntimeException {
	final ErrorCode errorCode;

	public HeatmapException(final ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
}
