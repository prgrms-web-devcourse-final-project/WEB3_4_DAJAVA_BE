package com.dajava.backend.domain.heatmap.exception;

import com.dajava.backend.global.exception.ErrorCode;

public class HeatmapException extends RuntimeException {
	public final ErrorCode errorCode;

	public HeatmapException(final ErrorCode errorCode) {
		super(errorCode.getDescription());
		this.errorCode = errorCode;
	}
}
