package com.example.caffepopularproject.domain.point.service;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.point.entity.Point;
import com.example.caffepopularproject.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    /* 포인트 API
    * 포인트 적립 : 관리자가 유저에게 포인트를 충전해 줌
    */

    @Transactional
    public void chargePoint (Long userId, Long amount) {

        Point point = pointRepository.findByUserIdWithPessimisticLocke(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        // JPA 더티 체킹으로 DB 저장
        point.charge(amount);
    }
}
