package com.dajava.backend.domain.heatmap.service;

import static com.dajava.backend.global.exception.ErrorCode.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.entity.SolutionEvent;
import com.dajava.backend.domain.event.repository.SolutionDataRepository;
import com.dajava.backend.domain.heatmap.dto.GridCell;
import com.dajava.backend.domain.heatmap.dto.HeatmapMetadata;
import com.dajava.backend.domain.heatmap.dto.HeatmapResponse;
import com.dajava.backend.domain.heatmap.exception.HeatmapException;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.solution.exception.SolutionException;
import com.dajava.backend.global.utils.PasswordUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 그리드 데이터 생성을 위한 서비스 로직
 * 모든 SolutionEvent 를 각 타입별로 구분하여 그리드 데이터를 누적합니다.
 * 30분 이내에 동일한 요청이 들어오면 캐싱된 데이터를 반환합니다.
 * @author Metronon
 * @since 2025-04-03
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HeatmapServiceImpl implements HeatmapService {

	private final RegisterRepository registerRepository;
	private final SolutionDataRepository solutionDataRepository;

	// 고정 그리드 사이즈
	// 추후 기능 확장시 해당 사이즈를 인자로 받아 조정하도록 만들 계획
	private static final int GRID_SIZE = 10;

	@Override
	@Cacheable(value = "heatmapCache", key = "{#serialNumber, #type}")
	@Transactional(readOnly = true)
	public HeatmapResponse getHeatmap(String serialNumber, String password, String type) {
		long startTime = System.currentTimeMillis();
		try {
			Register findRegister = registerRepository.findBySerialNumber(serialNumber)
				.orElseThrow(() -> new SolutionException(SOLUTION_SERIAL_NUMBER_INVALID));

			// 해싱된 password 로 접근 권한 확인
			if (!PasswordUtils.verifyPassword(password, findRegister.getPassword())) {
				throw new SolutionException(SOLUTION_PASSWORD_INVALID);
			}

			// SolutionData 가져오기
			SolutionData solutionData = solutionDataRepository.findBySerialNumber(serialNumber)
				.orElseThrow(() -> new SolutionException(SOLUTION_DATA_NOT_FOUND));

			// SolutionEvent 의 List 가져오기
			List<SolutionEvent> events = solutionData.getSolutionEvents();
			if (events.isEmpty()) {
				throw new SolutionException(SOLUTION_EVENT_DATA_NOT_FOUND);
			}

			// 이벤트 샘플링으로 데이터가 방대한 경우 반환 시간 최적화
			if (events.size() > 1000) {
				events = sampleEvents(events, type);
			}

			// 그리드 생성 로직으로 결과값 생성
			HeatmapResponse response;
			if ("scroll".equalsIgnoreCase(type)) {
				events.sort(Comparator.comparing(SolutionEvent::getTimestamp));
				response = createScrollDepthHeatmap(events);
			} else if ("click".equalsIgnoreCase(type) || "mousemove".equalsIgnoreCase(type)) {
				response = createCoordinateHeatmap(events, type);
			} else {
				throw new HeatmapException(INVALID_EVENT_TYPE);
			}

			// toBuilder 를 통해 pageCapture 경로값 추가
			response = response.toBuilder()
				.pageCapture(findRegister.getPageCapture())
				.build();

			// 소요 시간 측정
			long endTime = System.currentTimeMillis();
			log.info("히트맵 생성 성능 분석 결과: 일련 번호={}, type={}, totalEvent={}, 소요시간={}ms",
				serialNumber, type, events.size(), (endTime - startTime)
			);

			return response;
		} catch (SolutionException e) {
			long endTime = System.currentTimeMillis();
			log.info("히트맵 생성 성능 분석 결과: 일련 번호={}, type={}, 소요시간={}ms, 오류={}",
				serialNumber, type, (endTime - startTime), e.getMessage(), e
			);
			throw e;
		}
	}

	/**
	 * 빈 히트맵 응답을 생성하는 로직입니다.
	 * 솔루션에 대해 이벤트가 존재하지 않는 경우 빈 응답을 반환합니다.
	 */
	private HeatmapResponse createEmptyHeatmapResponse() {
		return HeatmapResponse.builder()
			.gridSize(GRID_SIZE)
			.pageWidth(1024)
			.pageHeight(1024) // 기본값
			.gridCells(Collections.emptyList())
			.metadata(HeatmapMetadata.builder()
				.maxCount(0)
				.totalEvents(0)
				.pageUrl("unknown")
				.totalSessions(0)
				.build())
			.build();
	}

	/**
	 * 이벤트 목록에서 샘플링을 수행합니다.
	 * 이벤트 수가 많을 경우 모든 이벤트를 처리하는 대신 일부만 샘플링하여 효율성을 높힐 수 있습니다.
	 */
	private List<SolutionEvent> sampleEvents(List<SolutionEvent> events, String eventType) {
		int sampleRate;

		if ("mousemove".equalsIgnoreCase(eventType)) {
			sampleRate = events.size() > 10000 ? 20 : 10; // 이동 이벤트가 10000개 이상 ? 20 : 1 / 10 : 1
		} else if ("scroll".equalsIgnoreCase(eventType)) {
			sampleRate = 5; // 스크롤 이벤트 5 : 1
		} else {
			sampleRate = 2; // 클릭 이벤트 2 : 1
		}

		List<SolutionEvent> sampledEvents = new ArrayList<>();
		for (int i = 0; i < events.size(); i++) {
			if (i % sampleRate == 0) {
				sampledEvents.add(events.get(i));
			}
		}

		log.info("이벤트 샘플링 적용: {} 이벤트 {} -> {}", eventType, events.size(), sampledEvents.size());
		return sampledEvents;
	}

	/**
	 * 클릭 및 이동 타입 히트맵 생성 로직
	 * 타입에 맞는 이벤트에 따라 분석해 히트맵 데이터를 반환합니다.
	 *
	 * @param events serialNumber 를 통해 가져온 세션 데이터
	 * @param type 세션 데이터에서 추출할 로그 데이터의 타입
	 * @return HeatmapResponse 그리드 데이터와 메타 데이터를 포함한 히트맵 응답 DTO
	 */
	private HeatmapResponse createCoordinateHeatmap(List<SolutionEvent> events, String type) {
		// 이벤트가 없을시 빈 히트맵 정보 반환
		if (events.isEmpty()) {
			return createEmptyHeatmapResponse();
		}

		// 전체 페이지 크기 초기화
		int maxPageWidth = 0;
		int maxPageHeight = 0;

		// 그리드 맵 - 좌표를 키로 사용하는 HashMap
		Map<String, Integer> gridMap = new HashMap<>();

		int totalEvents = 0;
		LocalDateTime firstEventTime = null;
		LocalDateTime lastEventTime = null;

		// 첫번째 이벤트에서 페이지 URL 설정
		String pageUrl = events.getFirst().getPageUrl();

		// 세션을 구분하기 위한 고유 식별자 저장 HashSet
		Set<String> sessionIds = new HashSet<>();

		for (SolutionEvent event : events) {
			// 타입값이 null 이거나 목표 타입이 아니면 건너뜀
			if (event.getType() == null || !event.getType().equalsIgnoreCase(type)) {
				continue;
			}

			// 총 세션수를 count 하기 위해 HashSet 에 추가함
			if (event.getSessionId() != null) {
				sessionIds.add(event.getSessionId());
			}

			// 좌표값이 없다면 건너뜀
			if (event.getClientX() == null || event.getClientY() == null) {
				continue;
			}

			int x = event.getClientX();
			int y = event.getClientY() + event.getScrollY();

			// 페이지 크기 업데이트
			maxPageWidth = Math.max(maxPageWidth, event.getBrowserWidth());
			maxPageHeight = Math.max(maxPageHeight, event.getScrollHeight());

			// 이벤트 시간 업데이트
			if (firstEventTime == null || event.getTimestamp().isBefore(firstEventTime)) {
				firstEventTime = event.getTimestamp();
			}
			if (lastEventTime == null || event.getTimestamp().isAfter(lastEventTime)) {
				lastEventTime = event.getTimestamp();
			}

			// 그리드 좌표 계산
			int gridX = x / GRID_SIZE;
			int gridY = y / GRID_SIZE;
			String gridKey = gridX + ":" + gridY;

			// 해당 그리드 셀 카운트 증가
			gridMap.put(gridKey, gridMap.getOrDefault(gridKey, 0) + 1);
			totalEvents++;
		}

		// 최대 카운트 값
		int maxCount = gridMap.values().stream().max(Integer::compareTo).orElse(0);

		// 그리드 셀 리스트 생성
		List<GridCell> gridCells = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : gridMap.entrySet()) {
			String[] coordinates = entry.getKey().split(":");
			int gridX = Integer.parseInt(coordinates[0]);
			int gridY = Integer.parseInt(coordinates[1]);
			int count = entry.getValue();

			// 최대 카운트 값 대비 강도 계산
			int intensity = maxCount > 0 ? (int)(((double)count / maxCount) * 100) : 0;

			gridCells.add(GridCell.builder()
				.gridX(gridX)
				.gridY(gridY)
				.count(count)
				.intensity(intensity)
				.build());
		}

		int totalSessions = sessionIds.size();

		// 메타데이터 생성
		HeatmapMetadata metadata = HeatmapMetadata.builder()
			.maxCount(maxCount)
			.totalEvents(totalEvents)
			.pageUrl(pageUrl != null ? pageUrl : "unknown")
			.totalSessions(totalSessions)
			.firstEventTime(firstEventTime)
			.lastEventTime(lastEventTime)
			.build();

		// Heatmap Response 생성
		return HeatmapResponse.builder()
			.gridSize(GRID_SIZE)
			.pageWidth(maxPageWidth)
			.pageHeight(maxPageHeight)
			.gridCells(gridCells)
			.metadata(metadata)
			.build();
	}

	/**
	 * Scroll Depth 히트맵 생성 로직
	 * 전체 이벤트 타입의 로그에 대해 화면 체류 시간을 측정해 히트맵 데이터를 반환합니다.
	 *
	 * @param events serialNumber 를 통해 가져온 세션 데이터
	 * @return HeatmapResponse 그리드 데이터와 메타 데이터를 포함한 히트맵 응답 DTO
	 */
	private HeatmapResponse createScrollDepthHeatmap(List<SolutionEvent> events) {
		// 이벤트가 없을시 빈 히트맵 정보 반환
		if (events.isEmpty()) {
			return createEmptyHeatmapResponse();
		}

		int maxPageWidth = 0;
		int maxPageHeight = 0;

		// 첫번째 이벤트에서 페이지 URL 설정
		String pageUrl = events.getFirst().getPageUrl();

		// 시간순 정렬로 데이터를 가져오므로, 첫 데이터와 마지막 데이터로 시간 설정
		LocalDateTime firstEventTime = events.getFirst().getTimestamp();
		LocalDateTime lastEventTime = events.getLast().getTimestamp();

		// 화면 체류 시간 저장을 위한 HashMap
		Map<Integer, Long> durationByGridY = new HashMap<>();

		// 세션을 구분하기 위한 고유 식별자 저장 HashSet
		Set<String> sessionIds = new HashSet<>();

		// 시간 간격 비교를 위한 직전 이벤트
		SolutionEvent prevEvent = events.getFirst();

		// 첫번째 데이터 로그의 sessionId 를 HashSet 에 저장
		sessionIds.add(prevEvent.getSessionId());

		// 전체 페이지 크기 업데이트
		if (prevEvent.getBrowserWidth() != null) {
			maxPageWidth = Math.max(maxPageWidth, prevEvent.getBrowserWidth());
		}
		if (prevEvent.getScrollHeight() != null) {
			maxPageHeight = Math.max(maxPageHeight, prevEvent.getScrollHeight());
		} else if (prevEvent.getViewportHeight() != null) {
			maxPageHeight = Math.max(maxPageHeight, prevEvent.getViewportHeight());
		}

		// event 리스트에서 전후 데이터의 타임스탬프를 비교해 grid 정보를 생성하는 로직
		for (int i = 1; i < events.size(); i++) {
			SolutionEvent cntEvent = events.get(i);

			// 총 세션수를 count 하기 위해 HashSet 에 추가함
			if (cntEvent.getSessionId() != null) {
				sessionIds.add(cntEvent.getSessionId());
			}

			// 전체 페이지 크기 업데이트
			if (cntEvent.getBrowserWidth() != null) {
				maxPageWidth = Math.max(maxPageWidth, cntEvent.getBrowserWidth());
			}
			if (cntEvent.getScrollHeight() != null) {
				maxPageHeight = Math.max(maxPageHeight, cntEvent.getScrollHeight());
			} else if (cntEvent.getViewportHeight() != null) {
				maxPageHeight = Math.max(maxPageHeight, cntEvent.getViewportHeight());
			}

			// 두 이벤트 시간 간격 계산
			long duration = Duration.between(prevEvent.getTimestamp(), cntEvent.getTimestamp()).toMillis();

			// 이벤트 시간 간격이 30초 이상인 경우 5초로 재설정
			if (duration > 30000) {
				duration = 5000;
			}

			// 이전 이벤트 위치의 Y 좌표에 따른 화면 Top, Bottom 설정
			int viewportTop = prevEvent.getScrollY() != null ? prevEvent.getScrollY() : 0;
			int viewportHeight = prevEvent.getViewportHeight() != null ? prevEvent.getViewportHeight() : 1024;
			int viewportBottom = viewportTop + viewportHeight;

			// 화면 그리드 단위로 처리하기 위해 범위 지정
			int gridYStart = viewportTop / GRID_SIZE;
			int gridYEnd = viewportBottom / GRID_SIZE;

			// 지정된 범위로 각 그리드에 체류 시간 설정
			for (int gridY = gridYStart; gridY < gridYEnd; gridY++) {
				durationByGridY.put(gridY, durationByGridY.getOrDefault(gridY, 0L) + duration);
			}

			prevEvent = cntEvent;
		}

		// 최대 체류 시간
		long maxDuration = durationByGridY.values().stream().max(Long::compareTo).orElse(1L);

		// 그리드 셀 리스트 생성
		List<GridCell> gridCells = new ArrayList<>();
		for (Map.Entry<Integer, Long> entry : durationByGridY.entrySet()) {
			int gridY = entry.getKey();
			long duration = entry.getValue();

			// 페이지 width 를 그리드 단위로 계산
			int widthInGrids = Math.max(1, maxPageWidth / GRID_SIZE);

			// 최대 체류 시간 대비 강도 계산
			int intensity = (int)((duration * 100.0) / maxDuration);

			// 전체 페이지 width 로 히트맵 생성
			for (int gridX = 0; gridX < widthInGrids; gridX++) {
				gridCells.add(GridCell.builder()
					.gridX(gridX)
					.gridY(gridY)
					.count(duration > 100 ? (int)(duration / 100.0) : 1)
					.intensity(intensity)
					.build());
			}
		}

		int totalSessions = sessionIds.size();

		// 메타데이터 생성
		HeatmapMetadata metadata = HeatmapMetadata.builder()
			.maxCount((int)(maxDuration / 100))
			.totalEvents(events.size())
			.pageUrl(pageUrl != null ? pageUrl : "unknown")
			.totalSessions(totalSessions)
			.firstEventTime(firstEventTime)
			.lastEventTime(lastEventTime)
			.build();

		// Heatmap Response 생성
		return HeatmapResponse.builder()
			.gridSize(GRID_SIZE)
			.pageWidth(maxPageWidth)
			.pageHeight(maxPageHeight)
			.gridCells(gridCells)
			.metadata(metadata)
			.build();
	}
}
