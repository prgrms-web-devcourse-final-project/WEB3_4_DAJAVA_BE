package com.dajava.backend.domain.home.service;

import org.springframework.stereotype.Service;

import com.dajava.backend.domain.home.Sample;
import com.dajava.backend.domain.home.converter.SampleConverter;
import com.dajava.backend.domain.home.dto.SampleRequest;
import com.dajava.backend.domain.home.dto.SampleResponse;
import com.dajava.backend.domain.home.repository.SampleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SampleServiceImpl
 * SampleService 인터페이스 구현체
 *
 * @author ChoiHyunSan
 * @since 2025-03-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SampleServiceImpl implements SampleService {

	private final SampleRepository sampleRepository;

	/**
	 * Sample 로직 메서드
	 * @param request SampleRequest (DTO)
	 * @return SampleResponse (DTO)
	 */
	@Override
	public SampleResponse sampleLogic(SampleRequest request) {
		log.info("Call sample logic");

		Sample sample = Sample.create(request.content());
		sampleRepository.save(sample);

		return SampleConverter.toSampleResponse(sample);
	}
}
