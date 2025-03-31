package com.dajava.backend.global.component.analyzer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "analyzer.click")
@Data
@Component
public class ClickAnalyzerProperties {
	private int timeThresholdMs;
	private int positionThresholdPx;
	private int minClickCount;
}
