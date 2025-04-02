package com.dajava.backend.domain.event.es.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(indexName = "solution-event")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SolutionEventDocument {

	@Id
	private String id;

	@Field(type = FieldType.Keyword)
	private String sessionId;

	@Field(type = FieldType.Keyword)
	private String pageUrl;

	@Field(type = FieldType.Keyword)
	private String type; // click, move, scroll 등

	@Field(type = FieldType.Integer)
	private Integer scrollY;

	@Field(type = FieldType.Integer)
	private Integer scrollHeight;

	@Field(type = FieldType.Integer)
	private Integer viewportHeight;

	@Field(type = FieldType.Integer)
	private Integer browserWidth;

	@Field(type = FieldType.Date)
	private Long timestamp;

	@Field(type = FieldType.Integer)
	private Integer clientX;

	@Field(type = FieldType.Integer)
	private Integer clientY;

	@Field(type = FieldType.Text)
	private String element;

	@Field(type = FieldType.Keyword)
	private String serialNumber; // 솔루션 조회시 사용

	@Field(type = FieldType.Boolean)
	private Boolean isOutlier;
}