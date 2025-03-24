package com.dajava.backend.domain.register.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dajava.backend.domain.register.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
