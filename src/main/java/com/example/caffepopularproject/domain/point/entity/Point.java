package com.example.caffepopularproject.domain.point.entity;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.order.entity.Order;
import com.example.caffepopularproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Point {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long currentlyPoint;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public static Point createWallet (User user) {
        Point point = new Point();
        point.currentlyPoint = 0L;
        point.user = user;

        return point;
    }

    // 관리자 포인트 충전
    public void charge(Long amount) {
        this.currentlyPoint += amount;
    }

    // 주문 시 포인트 차감
    public void use (Long amount) {
        if (this.currentlyPoint < amount) {
            throw  new ServiceException(ErrorCode.POINT_NOT_ENOUGH);
        }
        this.currentlyPoint -= amount;
    }
}
