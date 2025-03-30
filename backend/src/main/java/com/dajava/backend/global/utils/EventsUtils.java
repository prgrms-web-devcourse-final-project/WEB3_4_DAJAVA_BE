package com.dajava.backend.global.utils;

import java.util.Comparator;
import java.util.List;

import com.dajava.backend.domain.event.entity.PointerClickEvent;
import com.dajava.backend.domain.event.entity.PointerEvent;
import com.dajava.backend.domain.event.entity.PointerMoveEvent;
import com.dajava.backend.domain.event.entity.PointerScrollEvent;

public class EventsUtils {

	private EventsUtils() {
	}

	public static <T extends PointerEvent> void sortByCreateDateAsc(List<T> events) {
		if (events != null) {
			events.sort(Comparator.comparing(PointerEvent::getCreateDate));
		}
	}
}
