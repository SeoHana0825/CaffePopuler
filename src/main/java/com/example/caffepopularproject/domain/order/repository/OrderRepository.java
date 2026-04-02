package com.example.caffepopularproject.domain.order.repository;

import com.example.caffepopularproject.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository <Order, Long> {

    Optional<Order> findByIdAndUserId (Long orderId,Long userId);

    boolean existsByOrderNo (String orderNo);
}
