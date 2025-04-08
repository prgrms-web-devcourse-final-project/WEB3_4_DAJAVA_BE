package com.dajava.backend.redis.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;


import lombok.RequiredArgsConstructor;

/**
 * SessionData 가 현재 Cache 에 존재하는지 확인하고, 없을 시 생성 및 Cache 에 저장하는 로직입니다.
 * computeIfAbsent 로직을 통해 Cache 에 데이터가 존재하지 않을시 생성 및 Cache 에 올려놓습니다.
 *
 * @author jhon S
 */
@RequiredArgsConstructor
@Service
public class RedisSessionDataService {

	private final SessionDataDocumentRepository sessionDataDocumentRepository;
	private final RedisTemplate<String, Object> redisTemplate; // 여기 주입받기

	private String generateKey(SessionDataKey key) {
		return "sessionData:" + key.sessionId() + "|" + key.pageUrl() + "|" + key.memberSerialNumber();
	}

	public SessionDataDocument createOrFindSessionDataDocument(SessionDataKey key) {
		String redisKey = generateKey(key);

		// Redis 캐시 조회
		SessionDataDocument cached = (SessionDataDocument) redisTemplate.opsForValue().get(redisKey);
		if (cached != null) return cached;

		// ES 조회 or 새로 생성
		SessionDataDocument document = sessionDataDocumentRepository
			.findByPageUrlAndSessionIdAndMemberSerialNumber(key.pageUrl(), key.sessionId(), key.memberSerialNumber())
			.orElseGet(() -> sessionDataDocumentRepository.save(
				SessionDataDocument.create(
					key.sessionId(), key.memberSerialNumber(), key.pageUrl(), System.currentTimeMillis()
				)
			));

		// Redis 캐시에 저장 (1시간 TTL)
		redisTemplate.opsForValue().set(redisKey, document);
		redisTemplate.expire(redisKey, 1, TimeUnit.HOURS);

		return document;
	}

	public void removeFromEsCache(SessionDataKey key) {
		redisTemplate.delete(generateKey(key));
	}
}
