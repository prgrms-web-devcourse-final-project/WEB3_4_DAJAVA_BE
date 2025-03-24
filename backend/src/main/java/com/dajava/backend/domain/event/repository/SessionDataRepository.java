package com.dajava.backend.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dajava.backend.domain.event.SessionData;

/**
 * SessionDataRepository
 * SessionData 엔티티에 대한 데이터 JPA Repository
 *
 * @author NohDongHui
 * @since 2025-03-24
 */
public interface SessionDataRepository extends JpaRepository<SessionData, Long> {
}
