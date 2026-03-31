package com.example.caffepopularproject.domain.order.entity;

import com.example.caffepopularproject.common.entity.BaseDate;
import com.example.caffepopularproject.domain.payment.entity.Payment;
import com.example.caffepopularproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Order extends BaseDate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false, unique = true, length = 225)
    private String orderNo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItemList = new ArrayList<>();

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "payment_id")
    private Payment payment;

    public static Order register (
            String orderNo,
            User user
    ) {
        Order order = new Order();

        order.totalAmount = 0L;
        order.orderNo = orderNo != null ? orderNo : UUID.randomUUID().toString();
        order.status = OrderStatus.CART;
        order.user = user;

        return order;
    }

    // 주문 항목 추가
    public void addOrderItem (OrderItem orderItem) {
        this.orderItemList.add(orderItem);
        orderItem.assignOrder(this);
        this.totalAmount += (orderItem.getOrderPriceSnap() * orderItem.getQuantity());
    }

    public void updateStatus (OrderStatus status) {
        this.status = status;
    }
}
