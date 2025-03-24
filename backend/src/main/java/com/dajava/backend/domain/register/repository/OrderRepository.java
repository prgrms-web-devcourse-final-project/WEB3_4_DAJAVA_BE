package com.dajava.backend.domain.register.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dajava.backend.domain.register.entity.Order;

/**
 * OrderRepository
 * Order Entity 에 대한 Spring Data Jpa 인터페이스
 *
 * @author ChoiHyunSan
 * @since 2025-03-24
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
