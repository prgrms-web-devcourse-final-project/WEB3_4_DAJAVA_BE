package com.dajava.backend.domain.register.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dajava.backend.domain.register.entity.Register;

/**
 * RegisterRepository
 * Register Entity 에 대한 Spring Data Jpa 인터페이스
 *
 * @author ChoiHyunSan
 * @since 2025-03-24
 */
@Repository
public interface RegisterRepository extends JpaRepository<Register, Long> {

	/**
	 * Url 경로를 통해 이미 존재하는지 체크
	 * @param url 솔루션 대상 Url
	 * @param pastDate 재생성 가능 기준 날짜
	 * @return 존재 여부
	 */
	@Query("""
		  SELECT CASE 
		         WHEN COUNT(s) = 0 THEN true
		         WHEN (SELECT s2.endDate FROM Register s2 
		              WHERE s2.url = :url 
		              ORDER BY s2.createDate DESC LIMIT 1) < :pastDate THEN true
		         ELSE false
		         END
		  FROM Register s
		  WHERE s.url = :url
		""")
	boolean checkUrlAvailability(
		@Param("url") String url,
		@Param("pastDate") LocalDateTime pastDate);

	/**
	 * 현재 시각을 기준으로 서비스가 진행중인지 판단후 가져옵니다.
	 * @param currentTime1 startDate와 비교할 현재 시각입니다.
	 * @param currentTime2 endDate와 비교할 현재 시각입니다.
	 * @return List<Register> startDate <= now <= endDate 인 Register 데이터의 리스트입니다.
	 */
	List<Register> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
		LocalDateTime currentTime1,
		LocalDateTime currentTime2);

	Register findBySerialNumber(String serialNumber);

	/**
	 * Register 엔티티의 isServiceExpired 값이 true 인 데이터 조회
	 * @return List<Register>
	 */
	List<Register> findByIsServiceExpiredTrue();

}


