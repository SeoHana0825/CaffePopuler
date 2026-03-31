package com.example.caffepopularproject.domain.order.dto.request;

import com.example.caffepopularproject.domain.order.entity.OrderItem;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderCreateRequest {

    @NotBlank(message = "주문번호는 필수입니다.")
    private String orderNo;

    private String email;

    private List<OrderItemRequest> items;
}
