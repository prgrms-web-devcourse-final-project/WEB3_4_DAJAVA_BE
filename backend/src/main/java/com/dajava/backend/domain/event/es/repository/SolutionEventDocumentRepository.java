package com.dajava.backend.domain.event.es.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dajava.backend.domain.event.es.entity.SolutionEventDocument;

/**
 * SolutionEventDocumentRepository
 * 솔루션에 사용되는 이벤트 로그를 저장하는 ES 인덱스입니다.
 *
 * author ChoiHyunSan
 */
public interface SolutionEventDocumentRepository extends ElasticsearchRepository<SolutionEventDocument, Long> {

	/**
	 * 솔루션 단위로 저장된 이벤트 객체들을 모두 조회
	 *
	 * @param serialNumber 솔루션 시리얼 번호
	 * @return List<SolutionEventDocument>
	 */
	List<SolutionEventDocument> findBySerialNumber(String serialNumber);

	/**
	 * 솔루션 단위로 저장된 이벤트 객체들을 페이징하여 조회
	 * 한번에 모두 가져오는 것이 부담스러운 경우 해당 메서드를 사용하여 조회하는 것을 권장
	 * 정렬 정보를 같이 Pageable 객체에 담아서 전달하여 조회
	 *
	 * @param serialNumber 솔루션 시리얼 번호
	 * @param pageable 페이징 정보
	 * @return List<SolutionEventDocument>
	 */
	List<SolutionEventDocument> findBySerialNumber(String serialNumber, Pageable pageable);

	/**
	 * 이상치 유무에 따라 다른 값을 가져오고 싶은 경우에 사용
	 * 솔루션 이벤트 객체들의 이상치 유무로 모두 조회
	 *
	 * @param serialNumber 솔루션 시리얼 번호
	 * @param isOutlier 이상치 유무
	 * @return List<SolutionEventDocument>
	 */
	List<SolutionEventDocument> findBySerialNumberAndIsOutlier(String serialNumber, boolean isOutlier);

	/**
	 * 이상치 유무에 따라 다른 값을 가져오고 싶은 경우에 사용
	 * 한번에 모두 가져오는 것이 부담스러운 경우 해당 메서드를 사용하여 조회하는 것을 권장
	 * 정렬 정보를 같이 Pageable 객체에 담아서 전달하여 조회
	 *
	 * @param serialNumber 솔루션 시리얼 번호
	 * @param isOutlier 이상치 유무
	 * @param pageable 페이징 정보
	 * @return List<SolutionEventDocument>
	 */
	List<SolutionEventDocument> findBySerialNumberAndIsOutlier(
		String serialNumber,
		boolean isOutlier,
		Pageable pageable);
}
