package com.dajava.backend.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dajava.backend.domain.event.PointerEvent;
import com.dajava.backend.domain.event.SolutionData;

public interface SolutionDataRepository extends JpaRepository<SolutionData, Long> {
}
