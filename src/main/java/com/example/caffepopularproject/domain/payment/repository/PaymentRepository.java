package com.example.caffepopularproject.domain.payment.repository;

import com.example.caffepopularproject.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository <Payment, Long> {
}
