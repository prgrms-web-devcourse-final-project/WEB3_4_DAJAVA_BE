package com.dajava.backend.domain.solution.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dajava.backend.domain.solution.entity.Solution;

/**
 * SolutionRepository 
 * Solution Entity 에 대한 Spring Data Jpa 인터페이스
 *
 */
public interface SolutionRepository extends JpaRepository<Solution, Long> {

	/**
	 * Url 경로를 통해 이미 존재하는지 체크
	 * @param url 솔루션 대상 Url
	 * @return 존재 여부
	 */
	boolean existsByUrl(String url);
}
