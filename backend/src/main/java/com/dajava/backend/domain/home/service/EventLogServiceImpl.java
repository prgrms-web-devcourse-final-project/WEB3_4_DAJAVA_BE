package com.dajava.backend.domain.home.service;

import org.springframework.stereotype.Service;

import com.dajava.backend.domain.home.repository.SessionDataRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EventLogServiceImpl
 * EventLogService 인터페이스 구현체
 *
 * @author NohDongHui
 * @since 2025-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventLogServiceImpl {

	private final SessionDataRepository sessionDataRepository;


}

