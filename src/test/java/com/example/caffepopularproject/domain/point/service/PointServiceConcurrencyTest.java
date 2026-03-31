package com.example.caffepopularproject.domain.point.service;

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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PointServiceConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
            pointRepository.deleteAll();
            userRepository.deleteAll();

            User user = new User(
                    null,
                    "제시카 알바몬",
                    "test@test.com",
                    "010-0000-0000",
                    "12345678"
            );

            savedUser = userRepository.save(user);

            Point point = Point.createWallet(savedUser);
            pointRepository.save(point);
    }

    @AfterEach
    void shutDown() {
        pointRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("동시성 제어 - 100번 동시에 충전 요청이 와도 비관적 락으로 포인트 유실 방지")
    void chargePoint_Concurrency() throws InterruptedException {

        // given
        // 100명 동시 요청
        int threadCount = 100;

        // 멀티스레드 환경 구축 - 스레드 32개
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 100개의 스레드가 모두 끝날 때까지 대기
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 각 스레드가 1000원씩 충전 시도
                    pointService.chargePoint(savedUser.getId(), 1000L);
                } finally {
                    latch.countDown(); // 작업이 하나씩 끝날 때마다 숫자 감소
                }
            });
        }

        // 100개의 요청이 모두 끝날 때까지 메인 스레드 대기
        latch.await();

        // then
        // 100원씩 100번 충전, DB에서 다시 꺼낸 잔액은 무조건 100,000원이어야 한다
        Point findPoint = pointRepository.findByUserId(savedUser.getId()).orElseThrow();

        // 비관적 락이 없었을 경우 손실 발생 - 랜덤 금액 찍힘
        assertThat(findPoint.getCurrentlyPoint()).isEqualTo(100000L);

        }
    }
