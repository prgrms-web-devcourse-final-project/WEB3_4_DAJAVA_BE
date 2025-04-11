package com.dajava.backend.domain.log.service;

import com.dajava.backend.domain.log.dto.ClickEventRequest;
import com.dajava.backend.domain.log.dto.MovementEventRequest;
import com.dajava.backend.domain.log.dto.ScrollEventRequest;

public interface EventService {
	/**
	 * click 이벤트가 발생했을 때 Se
	 * @param clickEventRequest // click DTO
	 */
	void createClickEvent(ClickEventRequest clickEventRequest);
	/**
	 * 마우스 이동 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 * @param movementEventRequest // movement Dto
	 */
	void createMoveEvent(MovementEventRequest movementEventRequest);
	/**
	 * 스크롤 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 * @param scrollEventRequest // scroll Dto
	 */
	void createScrollEvent(ScrollEventRequest scrollEventRequest);
}
