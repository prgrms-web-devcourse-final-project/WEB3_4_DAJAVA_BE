package com.dajava.backend.global.component.analyzer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "analyzer.move")
@Data
@Component
public class MoveAnalyzerProperties {
	private long timeWindowMs;
	private int turnThreshold;
	private double angleThresholdDegrees;
}
