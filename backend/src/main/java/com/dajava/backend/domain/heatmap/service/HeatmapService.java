package com.dajava.backend.domain.heatmap.service;

import com.dajava.backend.domain.heatmap.dto.HeatmapResponse;

public interface HeatmapService {
	/**
	 *
	 * @param serialNumber 신청자에게 제공된 시리얼 넘버
	 * @param password 신청자가 작성한 비밀번호
	 * @param type 시각화 데이터의 타입 정보
	 * @return HeatmapResponse
	 */
	HeatmapResponse getHeatmap(String serialNumber, String password, String type);
}
