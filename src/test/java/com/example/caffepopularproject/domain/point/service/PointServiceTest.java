package com.example.caffepopularproject.domain.point.service;

import com.example.caffepopularproject.domain.point.entity.Point;
import com.example.caffepopularproject.domain.point.repository.PointRepository;
import com.example.caffepopularproject.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("포인트 충전 성공 - 기존 잔액에 충전 금액이 정확이 충전 됨")
    void chargePoint_Success() {

        // given
        User testUser = new User(
                1L,
                "제시카 알바몬",
                "test@test.com",
                "010-0000-0000",
                "12345678"
        );

        Point point = Point.createWallet(testUser);

        given(pointRepository.findByUserIdWithPessimisticLock(anyLong()))
                .willReturn(Optional.of(point));

        // when
        pointService.chargePoint(1L, 100000L);

        // then
        assertThat(point.getCurrentlyPoint()).isEqualTo(100000L);
    }
}
