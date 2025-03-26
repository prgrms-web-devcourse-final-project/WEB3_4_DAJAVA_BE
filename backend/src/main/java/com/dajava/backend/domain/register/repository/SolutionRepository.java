package com.dajava.backend.domain.register.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dajava.backend.domain.register.entity.Solution;

/**
 * SolutionRepository
 * Solution Entity 에 대한 Spring Data Jpa 인터페이스
 *
 * @author ChoiHyunSan
 * @since 2025-03-24
 */
@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {

	/**
	 * Url 경로를 통해 이미 존재하는지 체크
	 * @param url 솔루션 대상 Url
	 * @param pastDate 재생성 가능 기준 날짜
	 * @return 존재 여부
	 */
	@Query("""
		      select count(s) = 0 or 
		             (select s2.endDate from Solution s2 
		              where s2.url = :url 
		              order by s2.createDate desc limit 1) < :pastDate
		      from Solution s
		      where s.url = :url
		""")
	boolean checkUrlAvailability(
		@Param("url") String url,
		@Param("pastDate") LocalDateTime pastDate);
}


