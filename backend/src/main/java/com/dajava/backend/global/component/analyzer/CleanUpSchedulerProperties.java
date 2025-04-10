package com.dajava.backend.global.component.analyzer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "cleanup")
public class CleanUpSchedulerProperties {

	private SchedulerDuration schedulerDuration = new SchedulerDuration();
	private SoftDeleteDay softDeleteDay = new SoftDeleteDay();

	@Data
	public static class SchedulerDuration {
		private String register;
		private String log;
		private String solution;
	}

	@Data
	public static class SoftDeleteDay {
		private int register;
		private int log;
		private int solution;
	}
}
