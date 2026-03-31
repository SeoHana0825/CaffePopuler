package com.example.caffepopularproject.domain.order.service;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.order.entity.Order;
import com.example.caffepopularproject.domain.menu.entity.Menu;
import com.example.caffepopularproject.domain.menu.repository.MenuRepository;
import com.example.caffepopularproject.domain.order.dto.request.OrderCreateRequest;
import com.example.caffepopularproject.domain.order.dto.request.OrderItemRequest;
import com.example.caffepopularproject.domain.order.repository.OrderRepository;
import com.example.caffepopularproject.domain.user.entity.User;
import com.example.caffepopularproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 생성 성공 - 메뉴 가격에 맞게 정확한 총액 계산")
    void createOrder_Success() {

        // given
        String uuid = UUID.randomUUID().toString();
        OrderCreateRequest request = new OrderCreateRequest(
                uuid,
                "test@test.com",
                List.of(
                        new OrderItemRequest(1L, 2L),
                        new OrderItemRequest(2L, 1L)
                )
        );

        User testUser = new User(
                1L,
                "제시카 알바몬",
                "test@test.com",
                "010-0000-0000",
                "12345678"
        );

        Menu menu1 = Menu.register(
                "아메리카노",
                4500L,
                100L
        );

        Menu menu2 = Menu.register(
                "카푸치노",
                5000L,
                50L
        );

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(testUser));
        given(orderRepository.existsByOrderNo(anyString())).willReturn(false);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu1));
        given(menuRepository.findById(2L)).willReturn(Optional.of(menu2));

        // orderRepository.save()를 호출하면 파라미터로 들어왔던 첫 번째 order 객체 그대로 반환
        given(orderRepository.save(any(Order.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        orderService.createOrder(request);

        // then
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order savdOrder = orderCaptor.getValue();

        assertThat(savdOrder.getTotalAmount()).isEqualTo(14000L);
        assertThat(savdOrder.getOrderItemList().size()).isEqualTo(2);
        assertThat(savdOrder.getOrderNo()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("주문 생성 실패 - 동일한 주문번호 요청 시 중볼 결제로 판단해 예외 처리")
    void createOrder_Fail_DuplicateUUID() {

        // given
        String uuid = "duplicate-uuid-1234";
        OrderCreateRequest request = new OrderCreateRequest(
                uuid,
                "test@test.com",
                List.of(new OrderItemRequest(1L, 1L))
        );

        User testUser = new User(
                1L,
                "제시카 알바몬",
                "test@test.com",
                "010-0000-0000",
                "12345678"
        );
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(testUser));
        given(orderRepository.existsByOrderNo(uuid)).willReturn(true);

        // then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.ORDER_DUPLICATE_NAME.getMessage());
    }
}
