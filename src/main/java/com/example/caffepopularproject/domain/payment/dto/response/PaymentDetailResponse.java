package com.example.caffepopularproject.domain.payment.dto.response;

import com.example.caffepopularproject.domain.payment.entity.Payment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class PaymentDetailResponse {

    private final Long id;
    private final Long orderId;
    private final Long totalAmount;

    public static PaymentDetailResponse from (Payment payment){
        return PaymentDetailResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .totalAmount(payment.getAmount())
                .build();
    }
}
