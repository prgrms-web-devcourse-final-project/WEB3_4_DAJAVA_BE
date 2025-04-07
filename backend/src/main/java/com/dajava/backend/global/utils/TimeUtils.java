package com.dajava.backend.global.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
	/**
	 * 두 LocalDateTime 사이의 시간 차이를 시간(hours) 단위의 int로 반환
	 *
	 * @param startTime 시작 시간
	 * @param endTime 끝 시간
	 * @return 두 시간 사이의 차이(시간 단위)
	 */
	public static int getDuration(LocalDateTime startTime, LocalDateTime endTime) {
		return (int)ChronoUnit.HOURS.between(startTime, endTime);
	}

	/**
	 * 타임스탬프를 localDateTime으로 변환
	 *
	 * @param timestamp long 형태 타임스탬프
	 * @return localDateTime 형식
	 */
	public static LocalDateTime convertLongToLocalDateTime(long timestamp) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
	}
}
