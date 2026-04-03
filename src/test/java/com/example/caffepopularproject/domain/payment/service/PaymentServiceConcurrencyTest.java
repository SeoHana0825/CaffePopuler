package com.example.caffepopularproject.domain.payment.service;

import com.example.caffepopularproject.domain.menu.repository.MenuRepository;
import com.example.caffepopularproject.domain.order.entity.Order;
import com.example.caffepopularproject.domain.order.repository.OrderRepository;
import com.example.caffepopularproject.domain.payment.repository.PaymentRepository;
import com.example.caffepopularproject.domain.point.entity.Point;
import com.example.caffepopularproject.domain.point.repository.PointRepository;
import com.example.caffepopularproject.domain.user.entity.User;
import com.example.caffepopularproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PaymentServiceConcurrencyTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    private Long savedUserId;
    private Long savedOrderId;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        pointRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User(
                null,
                "제시카 알바몬",
                "test@test.com",
                "010-0000-0000",
                "12345678"
        );

        userRepository.save(user);
        savedUserId = user.getId();

        Point point = Point.createWallet(user);
        point.charge(50000L);
        pointRepository.save(point);

        String uuid = UUID.randomUUID().toString();
        Order order = Order.register(uuid, user);

        orderRepository.save(order);
        savedOrderId = order.getId();
    }

    @AfterEach
    void shutDown() {
        paymentRepository.deleteAll();
        pointRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("결제 동시성 테스트 - 100명이 동시에 결제해도 1명만 결제 성공")
    void concurrentPaymentTest() throws InterruptedException {

        // given
        Long orderId = savedUserId;
        Long userId = savedOrderId;

        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    paymentService.payWithPoint (orderId, userId);

                    successCount.incrementAndGet();
                }catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        assertThat(successCount.get()).isEqualTo(1);

        assertThat(failCount.get()).isEqualTo(99);
    }

}
