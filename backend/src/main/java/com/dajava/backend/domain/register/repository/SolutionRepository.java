package com.dajava.backend.domain.register.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dajava.backend.domain.register.entity.Solution;

/**
 * SolutionRepository
 * Solution Entity 에 대한 Spring Data Jpa 인터페이스
 *
 */
public interface SolutionRepository extends JpaRepository<Solution, Long> {

	/**
	 * Url 경로를 통해 이미 존재하는지 체크
	 * @param url 솔루션 대상 Url
	 * @param curTime 재생성 가능 일자
	 * @param days endDate 로부터 지나야 하는 일수 크기
	 * @return 존재 여부
	 */
	@Query("""
			  select case when count(s) > 0 then true else false end from Solution s
			  where s.url = :url AND s.endDate < :curTime AND
			  FUNCTION('DATEDIFF', :curTime, s.endDate) >= :days
			  order by s.createDate desc
			  limit 1
		""")
	boolean checkUrlAvailability(
		@Param("url") String url,
		@Param("curTime") LocalDateTime curTime,
		@Param("days") int days);
}

