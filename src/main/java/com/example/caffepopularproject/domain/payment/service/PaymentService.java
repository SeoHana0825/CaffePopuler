package com.example.caffepopularproject.domain.payment.service;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.order.entity.Order;
import com.example.caffepopularproject.domain.order.entity.OrderStatus;
import com.example.caffepopularproject.domain.order.repository.OrderRepository;
import com.example.caffepopularproject.domain.payment.entity.Payment;
import com.example.caffepopularproject.domain.payment.repository.PaymentRepository;
import com.example.caffepopularproject.domain.payment.dto.response.PaymentDetailResponse;
import com.example.caffepopularproject.domain.point.entity.Point;
import com.example.caffepopularproject.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PointRepository pointRepository;

    /* 결제 API
     * 1. 결제 상제 조회
     * 2. 포인트 결제 (주문 조회 -> 중복 방지 -> 차감 -> 영수증 발행 -> 상태 변경)
    */

    @Transactional(readOnly = true)
    public PaymentDetailResponse getPaymentDetail(Long paymentId, Long userId) {
        Payment payment = paymentRepository.findByDbPaymentIdAndOrderUserId(paymentId, userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.PAYMENT_NOT_FOUND));

        return PaymentDetailResponse.from(payment);
    }

    @Transactional
    public void payWithPoint (Long orderId) {

        // 주문 조회
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDER_NOT_FOUND));

        // 이미 결제된 주문인지 확인 (중복 결제 확인)
        if (order.getStatus() != OrderStatus.CART) {
            throw  new ServiceException(ErrorCode.ORDER_DUPLICATE_NAME);
        }

        Point point = pointRepository.findByUserIdWithPessimisticLock(order.getUser().getId())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        // 포인트로 결제하기 (더티 채킹)
        point.use(order.getTotalAmount());

        // 영수증 발행 및 저장
        Payment payment = Payment.createPayment(order);
        paymentRepository.save(payment);

        // 주문서 상태 "결제 완료" 변경
        order.updateStatus(OrderStatus.COMPLETE);
    }
}
