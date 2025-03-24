package com.dajava.backend.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dajava.backend.domain.event.PointerScrollEvent;

/**
 * PointerScrollRepository
 * PointerScrollEvent 엔티티에 대한 데이터 JPA Repository
 * 사용 안하는 경우 없어질 예정
 * @author NohDongHui
 * @since 2025-03-24
 */
public interface PointerScrollEventRepository extends JpaRepository<PointerScrollEvent, Long> {
}
