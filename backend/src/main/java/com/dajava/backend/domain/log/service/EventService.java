package com.dajava.backend.domain.log.service;

import com.dajava.backend.domain.log.dto.ClickEventRequest;
import com.dajava.backend.domain.log.dto.MovementEventRequest;
import com.dajava.backend.domain.log.dto.ScrollEventRequest;

public interface EventService {
	/**
	 * 클릭 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 * @param clickEventRequest // 요청할 데이터
	 * @return result // 처리된 응답 데이터
	 */
	void createClickEvent(ClickEventRequest clickEventRequest);
	/**
	 * 마우스 이동 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 * @param movementEventRequest // 요청할 데이터
	 * @return result // 처리된 응답 데이터
	 */
	void createMoveEvent(MovementEventRequest movementEventRequest);
	/**
	 * 스크롤 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 * @param scrollEventRequest // 요청할 데이터
	 * @return result // 처리된 응답 데이터
	 */
	void createScrollEvent(ScrollEventRequest scrollEventRequest);
}
