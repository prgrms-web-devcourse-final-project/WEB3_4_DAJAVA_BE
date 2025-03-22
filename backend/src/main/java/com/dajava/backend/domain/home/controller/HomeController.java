package com.dajava.backend.domain.home.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.home.dto.SampleRequest;
import com.dajava.backend.domain.home.dto.SampleResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class HomeController {

	@GetMapping("/")
	public SampleResponse home(
		@RequestBody SampleRequest request
	){
		SampleResponse response = new SampleResponse();
		log.info("Response Data : {}", request.toString());

		return response;
	}
}
