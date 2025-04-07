package com.dajava.backend.domain.event.es.service;

import java.util.List;

import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;

/**
 *
 * PointerEventDocument 도메인의 서비스 로직을 처리하는 인터페이스
 *
 * @author NohDongHui
 */
public interface PointerEventDocumentService {

	/**
	 * sessionId에 해당하는 ClickEventDocument를 내부적으로 배처 처리해 나눠 가져옴
	 * @param sessionId, batchSize
	 * @return sessionId에 해당하는 모든 pointerClickEventDocument
	 */
	public List<PointerClickEventDocument> fetchAllClickEventDocumentsBySessionId(String sessionId,int batchSize);

	/**
	 * sessionId에 해당하는 ClickEventDocument를 내부적으로 배처 처리해 나눠 가져옴
	 * @param sessionId, batchSize
	 * @return sessionId에 해당하는 모든 pointerMoveEventDocument
	 */
	public List<PointerMoveEventDocument> fetchAllMoveEventDocumentsBySessionId(String sessionId, int batchSize);

	/**
	 * sessionId에 해당하는 ClickEventDocument를 내부적으로 배처 처리해 나눠 가져옴
	 * @param sessionId, batchSize
	 * @return sessionId에 해당하는 모든 pointerScrollEventDocument
	 */
	public List<PointerScrollEventDocument> fetchAllScrollEventDocumentsBySessionId(String sessionId, int batchSize);

}
