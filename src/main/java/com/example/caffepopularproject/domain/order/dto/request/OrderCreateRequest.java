package com.example.caffepopularproject.domain.order.dto.request;

import com.example.caffepopularproject.domain.order.entity.OrderItem;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderCreateRequest {

    private String orderNo;

    private Long userId;

    private List<OrderItemRequest> items;
}
