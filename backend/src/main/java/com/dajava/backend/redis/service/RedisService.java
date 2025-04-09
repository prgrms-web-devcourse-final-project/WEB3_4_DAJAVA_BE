package com.dajava.backend.redis.service;

import org.hibernate.Session;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;

public interface RedisService {
	/**
	 * 클릭 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 * @param clickEventRequest // 요청할 데이터
	 * @return result // 처리된 응답 데이터
	 */
	void createClickEvent(PointerClickEventRequest clickEventRequest);
	/**
	 * 마우스 이동 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 * @param pointerMoveEventRequest // 요청할 데이터
	 * @return result // 처리된 응답 데이터
	 */
	void createMoveEvent(PointerMoveEventRequest pointerMoveEventRequest);
	/**
	 * 스크롤 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 * @param pointerScrollEventRequest // 요청할 데이터
	 * @return result // 처리된 응답 데이터
	 */
	void createScrollEvent(PointerScrollEventRequest pointerScrollEventRequest);
	void expireSession(String sessionId);
	void startSession(SessionDataKey sessionDataKey);
}
