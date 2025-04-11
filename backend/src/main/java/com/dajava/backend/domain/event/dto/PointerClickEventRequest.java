// package com.dajava.backend.domain.event.dto;
//
// import io.swagger.v3.oas.annotations.media.Schema;
// import jakarta.validation.constraints.NotNull;
// import lombok.Getter;
// import lombok.Setter;
//
// /**
//  * Click(Touch) 관련 요청 DTO 입니다.
//  * @author Metronon
//  */
// @Getter
// @Setter
// @Schema(description = "Click(Touch) 관련 요청 DTO 입니다.")
// public class PointerClickEventRequest {
//
// 	@NotNull
// 	@Schema(description = "랜덤으로 생성된 이벤트 식별자", example = "e25f6b52-4a1b-4721-8651-8839f23727cb", requiredMode = Schema.RequiredMode.REQUIRED)
// 	private String eventId;
//
// 	@NotNull
// 	@Schema(description = "사용자의 fingerPrint 로 생성된 식별자", example = "e25f6b52-4a1b-4721-8651-8839f23727cb", requiredMode = Schema.RequiredMode.REQUIRED)
// 	private String sessionId;
//
// 	@NotNull
// 	@Schema(description = "세션의 페이지 URL 정보", example = "localhost:3000/myPage", requiredMode = Schema.RequiredMode.REQUIRED)
// 	private String pageUrl;
//
// 	@NotNull
// 	@Schema(description = "행동 솔루션 신청시 생성된 UUID 식별자", example = "a07cb1fc-e5db-4578-89a6-34d7a31f9389", requiredMode = Schema.RequiredMode.REQUIRED)
// 	private String memberSerialNumber;
//
// 	@NotNull
// 	@Schema(description = "로그 데이터의 생성 시각", example = "1711963200000", requiredMode = Schema.RequiredMode.REQUIRED)
// 	private Long timestamp;
//
// 	@NotNull
// 	@Schema(description = "현재 브라우저 창의 가로 길이", example = "1280", requiredMode = Schema.RequiredMode.REQUIRED)
// 	private Integer browserWidth;
//
// 	@NotNull
// 	@Schema(description = "클릭시 X 좌표값", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
// 	private Integer clientX;
//
// 	@NotNull
// 	@Schema(description = "클릭시 Y 좌표값", example = "500", requiredMode = Schema.RequiredMode.REQUIRED)
// 	private Integer clientY;
//
// 	@NotNull
// 	@Schema(description = "현재 화면의 스크롤 상단 Y 좌표", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
// 	private Integer scrollY;
//
// 	@NotNull
// 	@Schema(description = "전체 페이지의 세로 길이", example = "1500", requiredMode = Schema.RequiredMode.REQUIRED)
// 	private Integer scrollHeight;
//
// 	@NotNull
// 	@Schema(description = "현재 브라우저 창의 세로 길이", example = "500", requiredMode = Schema.RequiredMode.REQUIRED)
// 	private Integer viewportHeight;
//
// 	@NotNull
// 	@Schema(description = "클릭시 대상의 태그 정보", example = "div", requiredMode = Schema.RequiredMode.REQUIRED)
// 	private String element;
//
// 	// 기본 생성자
// 	public PointerClickEventRequest() {
// 	}
//
// 	// 모든 필드를 포함한 생성자
// 	public PointerClickEventRequest(String eventId, String sessionId, String pageUrl, String memberSerialNumber,
// 		Long timestamp, Integer browserWidth, Integer clientX, Integer clientY,
// 		Integer scrollY, Integer scrollHeight, Integer viewportHeight, String element) {
// 		this.eventId = eventId;
// 		this.sessionId = sessionId;
// 		this.pageUrl = pageUrl;
// 		this.memberSerialNumber = memberSerialNumber;
// 		this.timestamp = timestamp;
// 		this.browserWidth = browserWidth;
// 		this.clientX = clientX;
// 		this.clientY = clientY;
// 		this.scrollY = scrollY;
// 		this.scrollHeight = scrollHeight;
// 		this.viewportHeight = viewportHeight;
// 		this.element = element;
// 	}
// }
