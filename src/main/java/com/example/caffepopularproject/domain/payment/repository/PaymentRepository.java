package com.example.caffepopularproject.domain.payment.repository;

import com.example.caffepopularproject.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository <Payment, Long> {

    Optional<Payment> findByDbPaymentIdAndOrderUserId(Long paymentId, Long userId);

}
