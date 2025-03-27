package com.dajava.backend.global.utils;

import java.time.LocalDateTime;
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
}
