package com.example.caffepopularproject.domain.payment.service;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.menu.entity.Menu;
import com.example.caffepopularproject.domain.order.entity.Order;
import com.example.caffepopularproject.domain.order.entity.OrderItem;
import com.example.caffepopularproject.domain.order.entity.OrderStatus;
import com.example.caffepopularproject.domain.order.repository.OrderRepository;
import com.example.caffepopularproject.domain.payment.entity.Payment;
import com.example.caffepopularproject.domain.payment.repository.PaymentRepository;
import com.example.caffepopularproject.domain.point.entity.Point;
import com.example.caffepopularproject.domain.point.repository.PointRepository;
import com.example.caffepopularproject.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("결제 성공 - 정상적인 포인트 차감 및 주문 상태 변경")
    void paymentWithPoint_Success() {

        // given
        Long userId = 1L;
        Long orderId = 10L;

        User user = new User(
                1L,
                "제시카 알바몬",
                "test@test.com",
                "010-0000-0000",
                "12345678"
        );

        Menu menu = Menu.register(
                "아메리카노",
                4500L,
                100L
        );

        OrderItem orderItem = OrderItem.register(
                2L,
                menu
        );

        Order order = Order.register(
                "duplicate-uuid-1234",
                user
        );

        Point point = Point.createWallet(user);
        point.charge(20000L);

        order.addOrderItem(orderItem);

        given(orderRepository.findByIdAndUserId(anyLong(),anyLong())).willReturn(Optional.of(order));
        given(pointRepository.findByUserIdWithPessimisticLock(anyLong())).willReturn(Optional.of(point));

        // when
        paymentService.payWithPoint(orderId, userId);

        // then
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        Payment savePayment = paymentCaptor.getValue();

        assertThat(savePayment.getAmount()).isEqualTo(9000L);
        assertThat(savePayment.getOrder().getOrderNo()).isEqualTo("duplicate-uuid-1234");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETE);

    }

    @Test
    @DisplayName("결제 실패 - 충전 포인트가 충분하지 않았을 때")
    void paymentWithPoint_Fail_NotEnoughPoint() {

        // given
        Long userId = 1L;
        Long orderId = 10L;

        User user = new User(
                1L,
                "제시카 알바몬",
                "test@test.com",
                "010-0000-0000",
                "12345678"
        );

        Menu menu = Menu.register(
                "아메리카노",
                4500L,
                100L
        );

        OrderItem orderItem = OrderItem.register(
                2L,
                menu
        );

        Order order = Order.register(
                "duplicate-uuid-1234",
                user
        );

        Point point = Point.createWallet(user);
        point.charge(5000L);

        order.addOrderItem(orderItem);

        given(orderRepository.findByIdAndUserId(anyLong(),anyLong())).willReturn(Optional.of(order));
        given(pointRepository.findByUserIdWithPessimisticLock(anyLong())).willReturn(Optional.of(point));

        // than
        assertThatThrownBy(() -> paymentService.payWithPoint(orderId, userId))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.POINT_NOT_ENOUGH.getMessage());
    }


}
