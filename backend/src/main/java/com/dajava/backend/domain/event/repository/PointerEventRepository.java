package com.dajava.backend.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dajava.backend.domain.event.PointerEvent;

public interface PointerEventRepository extends JpaRepository<PointerEvent, Long> {
}
