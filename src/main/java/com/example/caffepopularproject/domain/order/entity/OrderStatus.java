package com.example.caffepopularproject.domain.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    CART,
    PENDING,
    COMPLETE,
    CANCELED,
}
