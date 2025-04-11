package com.dajava.backend.global.utils;

import java.util.Comparator;
import java.util.List;

import com.dajava.backend.domain.event.entity.PointerClickEvent;
import com.dajava.backend.domain.event.entity.PointerEvent;
import com.dajava.backend.domain.event.entity.PointerMoveEvent;
import com.dajava.backend.domain.event.entity.PointerScrollEvent;
import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;

public class EventsUtils {

	private EventsUtils() {
	}

	public static <T extends PointerEvent> void sortByCreateDateAsc(List<T> events) {
		if (events != null) {
			events.sort(Comparator.comparing(PointerEvent::getCreateDate));
		}
	}

	public static List<PointerClickEventDocument> filterValidClickEvents(List<PointerClickEventDocument> events) {
		return events.stream()
			.filter(PointerClickEventDocument::isValid)
			.toList();
	}

	public static List<PointerMoveEventDocument> filterValidMoveEvents(List<PointerMoveEventDocument> events) {
		return events.stream()
			.filter(PointerMoveEventDocument::isValid)
			.toList();
	}

	public static List<PointerScrollEventDocument> filterValidScrollEvents(List<PointerScrollEventDocument> events) {
		return events.stream()
			.filter(PointerScrollEventDocument::isValid)
			.toList();
	}
}
