package com.example.caffepopularproject.domain.order.dto.response;

import com.example.caffepopularproject.domain.order.entity.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class OrderResponse {

    private final Long userId;
    private final Long id;
    private final Long totalAmount;
    private final String orderNo;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .userId(order.getUser().getId())
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .orderNo(order.getOrderNo())
                .build();
    }
}
