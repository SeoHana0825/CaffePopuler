package com.example.caffepopularproject.domain.payment.entity;

import com.example.caffepopularproject.domain.order.entity.Order;
import com.example.caffepopularproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "order_id", unique = true)
    private Order order;

    public static Payment register (Order order) {
        Payment payment = new Payment();

        payment.amount = order.getTotalAmount();
        payment.user = order.getUser();
        payment.order = order;

        return payment;
    }

    public static Payment createPayment (Order order) {
        Payment payment = new Payment();

        payment.amount = order.getTotalAmount();
        payment.user = order.getUser();

        return payment;
    }
}
