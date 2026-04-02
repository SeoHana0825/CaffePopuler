package com.example.caffepopularproject.domain.payment.dto;

import com.example.caffepopularproject.domain.payment.entity.Payment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder (access = AccessLevel.PRIVATE)
public class OrderCompletedEvent {

    private final String orderNo;
    private final Long userId;
    private final Long paymentAmount;

    public static OrderCompletedEvent from (Payment payment) {
        return OrderCompletedEvent.builder()
                .orderNo(payment.getOrder().getOrderNo())
                .userId(payment.getUser().getId())
                .paymentAmount(payment.getAmount())
                .build();
    }

}
