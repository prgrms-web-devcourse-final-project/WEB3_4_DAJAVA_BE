package com.dajava.backend.domain.event.es.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * api로 들어온 scrollEvent를 저장하는 ES 인덱스 입니다.
 * @author NohDongHui
 */
@Document(indexName = "pointer-scroll-events")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PointerScrollEventDocument {

	@Id
	private String id;

	private String sessionId;

	private String pageUrl;

	private String memberSerialNumber;

	private Long timestamp;

	private Integer browserWidth;

	private Integer scrollY;

	private Integer scrollHeight;

	private Integer viewportHeight;

}
