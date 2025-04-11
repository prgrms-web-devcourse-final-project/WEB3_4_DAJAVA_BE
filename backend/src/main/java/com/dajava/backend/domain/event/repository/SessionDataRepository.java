package com.dajava.backend.domain.event.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dajava.backend.domain.event.entity.SessionData;

/**
 * SessionDataRepository
 * SessionData 엔티티에 대한 데이터 JPA Repository
 *
 * @author NohDongHui
 * @since 2025-03-24
 */
public interface SessionDataRepository extends JpaRepository<SessionData, Long> {
	Optional<SessionData> findByPageUrlAndSessionIdAndMemberSerialNumber(
		String pageUrl, String sessionId, String memberSerialNumber
	);

	Optional<SessionData> findBySessionId(String sessionId);

	/**
	 * 종료된 세션 데이터를 조회
	 *
	 * @return List<SessionData>
	 */
	@Query("""
		SELECT s FROM SessionData s
			WHERE s.isSessionEnded = true
				ORDER BY s.modifiedDate ASC
		""")
	List<SessionData> findEndedSession();

	Optional<List<SessionData>> findByMemberSerialNumber(String memberSerialNumber);
}


