package com.dajava.backend.global.component.analyzer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "event.scheduler")
@Data
@Component
public class BufferSchedulerProperties {
	private String inactiveSessionDetectThresholdMs;
	private String activeSessionFlushIntervalMs;
	private long inactiveThresholdMs;
	private long validateEndSessionMs;
	private int batchSize ;
}
