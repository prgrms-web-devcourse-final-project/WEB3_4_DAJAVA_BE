package com.dajava.backend.domain.log.converter;

import java.util.ArrayList;
import java.util.List;

import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;
import com.dajava.backend.domain.event.es.entity.SolutionEventDocument;
import com.dajava.backend.domain.log.dto.ClickEventRequest;
import com.dajava.backend.domain.log.dto.MovementEventRequest;
import com.dajava.backend.domain.log.dto.ScrollEventRequest;
import com.dajava.backend.global.utils.TimeUtils;

	public class EventConverter {
		private EventConverter() {
		}
		public static PointerClickEventDocument toClickEventDocument(ClickEventRequest request) {
				return PointerClickEventDocument.builder()
					.id(request.getEventId() + request.getTimestamp())
					.sessionId(request.getSessionIdentifier().getSessionId())
					.pageUrl(request.getSessionIdentifier().getPageUrl())
					.memberSerialNumber(request.getSessionIdentifier().getMemberSerialNumber())
					.timestamp(request.getTimestamp())
					.browserWidth(request.getBrowserWidth())
					.clientX(request.getClientX())
					.clientY(request.getClientY())
					.scrollY(request.getScrollY())
					.scrollHeight(request.getScrollHeight())
					.viewportHeight(request.getViewportHeight())
					.element(request.getTag())
					.isOutlier(false)
					.build();
			}

			public static PointerMoveEventDocument toMoveEventDocument(MovementEventRequest request) {
				return PointerMoveEventDocument.builder()
					.id(request.getEventId() + request.getTimestamp())
					.sessionId(request.getSessionIdentifier().getSessionId())
					.pageUrl(request.getSessionIdentifier().getPageUrl())
					.memberSerialNumber(request.getSessionIdentifier().getMemberSerialNumber())
					.timestamp(request.getTimestamp())
					.browserWidth(request.getBrowserWidth())
					.clientX(request.getClientX())
					.clientY(request.getClientY())
					.scrollY(request.getScrollY())
					.scrollHeight(request.getScrollHeight())
					.viewportHeight(request.getViewportHeight())
					.isOutlier(false)
					.build();
			}

			public static PointerScrollEventDocument toScrollEventDocument(ScrollEventRequest request) {
				return PointerScrollEventDocument.builder()
					.id(request.getEventId() + request.getTimestamp())
					.sessionId(request.getSessionIdentifier().getSessionId())
					.pageUrl(request.getSessionIdentifier().getPageUrl())
					.memberSerialNumber(request.getSessionIdentifier().getMemberSerialNumber())
					.timestamp(request.getTimestamp())
					.browserWidth(request.getBrowserWidth())
					.scrollY(request.getScrollY())
					.scrollHeight(request.getScrollHeight())
					.viewportHeight(request.getViewportHeight())
					.isOutlier(false)
					.build();
			}

		public static SolutionEventDocument fromClickDocument(PointerClickEventDocument event) {

			return SolutionEventDocument.builder()
				.id(event.getId())
				.sessionId(event.getSessionId())
				.pageUrl(event.getPageUrl())
				.serialNumber(event.getMemberSerialNumber())
				.type("click")
				.clientX(event.getClientX())
				.clientY(event.getClientY())
				.timestamp(TimeUtils.toEpochMillis(event.getTimestamp()))
				.browserWidth(event.getBrowserWidth())
				.scrollY(event.getScrollY())
				.scrollHeight(event.getScrollHeight())
				.viewportHeight(event.getViewportHeight())
				.element(event.getElement()) // element를 tag로 쓸 경우
				.isOutlier(event.getIsOutlier())
				.build();
		}

		public static SolutionEventDocument fromMoveDocument(PointerMoveEventDocument event) {

			return SolutionEventDocument.builder()
				.id(event.getId())
				.sessionId(event.getSessionId())
				.pageUrl(event.getPageUrl())
				.serialNumber(event.getMemberSerialNumber())
				.type("move")
				.clientX(event.getClientX())
				.clientY(event.getClientY())
				.timestamp(TimeUtils.toEpochMillis(event.getTimestamp()))
				.browserWidth(event.getBrowserWidth())
				.scrollY(event.getScrollY())
				.scrollHeight(event.getScrollHeight())
				.viewportHeight(event.getViewportHeight())
				.isOutlier(event.getIsOutlier())
				.build();
		}

		public static SolutionEventDocument fromScrollDocument(PointerScrollEventDocument event) {

			return SolutionEventDocument.builder()
				.id(event.getId())
				.sessionId(event.getSessionId())
				.pageUrl(event.getPageUrl())
				.serialNumber(event.getMemberSerialNumber())
				.type("scroll")
				.clientX(null) // scroll 이벤트에는 X/Y 좌표가 없을 수도 있음
				.clientY(null)
				.timestamp(TimeUtils.toEpochMillis(event.getTimestamp()))
				.browserWidth(event.getBrowserWidth())
				.scrollY(event.getScrollY())
				.scrollHeight(event.getScrollHeight())
				.viewportHeight(event.getViewportHeight())
				.isOutlier(event.getIsOutlier())
				.build();
		}


			public static List<SolutionEventDocument> toSolutionEventDocuments(
				List<PointerClickEventDocument> clicks,
				List<PointerMoveEventDocument> moves,
				List<PointerScrollEventDocument> scrolls
			) {
				List<SolutionEventDocument> result = new ArrayList<>();

				result.addAll(clicks.stream()
					.map(EventConverter::fromClickDocument)
					.toList());

				result.addAll(moves.stream()
					.map(EventConverter::fromMoveDocument)
					.toList());

				result.addAll(scrolls.stream()
					.map(EventConverter::fromScrollDocument)
					.toList());

				return result;
			}
		}


