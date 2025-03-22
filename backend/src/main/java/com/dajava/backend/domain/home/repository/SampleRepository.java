package com.dajava.backend.domain.home.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dajava.backend.domain.home.Sample;

/**
 * SampleRepository
 * Sample 엔티티에 대한 데이터 JPA Repository
 *
 * @author ChoiHyunSan
 * @since 2025-03-22
 */
public interface SampleRepository extends JpaRepository<Sample, Long> {
}
