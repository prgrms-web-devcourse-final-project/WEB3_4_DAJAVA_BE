package com.dajava.backend.domain.home.converter;

import com.dajava.backend.domain.home.Sample;
import com.dajava.backend.domain.home.dto.SampleResponse;

/**
 * SampleConverter
 * Sample Domain 관련 변환 메서드를 모아둔 클래스
 *
 * @author ChoiHyunSan
 * @since 2025-03-22
 */
public class SampleConverter {

	public static SampleResponse toSampleResponse(Sample sample){
		return SampleResponse.builder()
			.content(sample.getContent())
			.build();
	}
}
