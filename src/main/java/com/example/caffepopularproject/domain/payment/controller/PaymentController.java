package com.example.caffepopularproject.domain.payment.controller;

import com.example.caffepopularproject.common.dto.ApiResponse;
import com.example.caffepopularproject.domain.payment.dto.PaymentDetailResponse;
import com.example.caffepopularproject.domain.payment.service.PaymentService;
import com.example.caffepopularproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 명세서 조회
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentDetailResponse>> getPaymentDetail (
            @SessionAttribute(name = "LOGIN_USER_ID", required = false) User user,
            @PathVariable Long paymentId
    ) {
        Long userId = user.getId();
        PaymentDetailResponse response = paymentService.getPaymentDetail(paymentId, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // 결제 진행
    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> payWithPoint (
            @SessionAttribute(name = "LOGIN_USER_ID", required = false) User user,
            @PathVariable Long orderId
    ) {
        Long userId = user.getId();

        paymentService.payWithPoint(orderId, userId);

        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
