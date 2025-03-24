package com.dajava.backend.domain.register.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dajava.backend.domain.register.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
