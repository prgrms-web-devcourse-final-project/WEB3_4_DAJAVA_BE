package com.dajava.backend.domain.home.service;

import com.dajava.backend.domain.home.dto.SampleRequest;
import com.dajava.backend.domain.home.dto.SampleResponse;

/**
 * SampleService
 * Sample Domain 의 비즈니스 로직을 처리하는 인터페이스
 *
 * @author ChoiHyunSan
 * @since 2025-03-22
 */
public interface SampleService {
	SampleResponse sampleLogic(SampleRequest response);
}
