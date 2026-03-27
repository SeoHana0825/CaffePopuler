package com.example.caffepopularproject.domain.order.repository;

import com.example.caffepopularproject.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository <Order, Long> {
}
