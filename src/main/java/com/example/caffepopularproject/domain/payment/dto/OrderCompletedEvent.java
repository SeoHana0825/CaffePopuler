package com.example.caffepopularproject.domain.payment.dto;

import com.example.caffepopularproject.domain.menu.dto.response.MenuRankInfo;
import com.example.caffepopularproject.domain.payment.entity.Payment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder (access = AccessLevel.PRIVATE)
public class OrderCompletedEvent {

    private final String orderNo;
    private final Long userId;
    private final Long paymentAmount;
    private final List<MenuRankInfo> MenuRankInfos;

    public static OrderCompletedEvent from (Payment payment) {
        return OrderCompletedEvent.builder()
                .orderNo(payment.getOrder().getOrderNo())
                .userId(payment.getUser().getId())
                .paymentAmount(payment.getAmount())
                .MenuRankInfos(
                        payment.getOrder().getOrderItemList().stream()
                                .map(item -> new MenuRankInfo(item.getNameSnap(),item.getQuantity()))
                                .collect(Collectors.toList())
                ).build();
    }
}
