package com.example.caffepopularproject.domain.order.entity;

import com.example.caffepopularproject.common.entity.BaseDate;
import com.example.caffepopularproject.domain.menu.entity.Menu;
import com.example.caffepopularproject.domain.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Order extends BaseDate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long order_price;

    @Column(nullable = false)
    private Long total_amount;

    @Column(nullable = false, length = 225)
    private String order_no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "menu_id")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "payment_id")
    private Payment payment;

    public static Order register (
            Long order_price,
            Long total_amount,
            String order_no,
            Menu menu,
            Payment payment
    ) {
        Order order = new Order();

        order.order_price = order_price;
        order.total_amount = total_amount;
        order.order_no = order_no;
        order.menu = menu;
        order.payment = payment;

        return order;
    }
}
