package com.dajava.backend.domain.solution.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dajava.backend.domain.solution.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
