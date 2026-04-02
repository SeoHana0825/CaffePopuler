package com.example.caffepopularproject.domain.order.service;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.menu.entity.Menu;
import com.example.caffepopularproject.domain.menu.repository.MenuRepository;
import com.example.caffepopularproject.domain.order.dto.request.OrderCreateRequest;
import com.example.caffepopularproject.domain.order.dto.request.OrderItemRequest;
import com.example.caffepopularproject.domain.order.dto.response.OrderResponse;
import com.example.caffepopularproject.domain.order.entity.Order;
import com.example.caffepopularproject.domain.order.entity.OrderItem;
import com.example.caffepopularproject.domain.order.repository.OrderRepository;
import com.example.caffepopularproject.domain.user.entity.User;
import com.example.caffepopularproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    /* 주문 API
     * 1. 주문 생성
     * 2. 주문 상태 변경
     * */

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        // 따닥 이슈 이중 방어
        if (orderRepository.existsByOrderNo(request.getOrderNo())) {
            throw new ServiceException(ErrorCode.ORDER_DUPLICATE_NAME);
        }

        Order order = Order.register(request.getOrderNo(), user);

        for (OrderItemRequest itemRequest : request.getItems()) {
            Menu menu = menuRepository.findById(itemRequest.getMenuId())
                    .orElseThrow(() -> new ServiceException(ErrorCode.MENU_NOT_FOUND));

            // 스냅샷 및 orderItem 생성
            OrderItem orderItem = OrderItem.register(itemRequest.getQuantity(), menu);

            order.addOrderItem(orderItem);
        }

        Order savdOrder = orderRepository.save(order);

        return OrderResponse.from(savdOrder);
    }
}
