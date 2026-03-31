package com.example.caffepopularproject.domain.order.entity;

import com.example.caffepopularproject.domain.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nameSnap;

    @Column(nullable = false)
    private Long orderPriceSnap;

    @Column(nullable = false)
    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "menu_id")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "order_id")
    private Order order;

    public static OrderItem register(
            Long quantity,
            Menu menu
    ) {
        OrderItem orderItem = new OrderItem();

        orderItem.nameSnap = menu.getName();
        orderItem.orderPriceSnap = menu.getPrice();
        orderItem.quantity = quantity;
        orderItem.menu = menu;

        return orderItem;
    }

    public void assignOrder(Order order) {
        this.order = order;
    }
}
