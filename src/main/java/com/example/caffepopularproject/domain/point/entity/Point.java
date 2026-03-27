package com.example.caffepopularproject.domain.point.entity;

import com.example.caffepopularproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "points")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long charged_point;

    @Column(nullable = false)
    private Long used_point;

    @Column(nullable = false)
    private Long total_point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Point register (
            Long charged_point,
            Long used_point,
            Long total_point,
            User user
    ) {
        Point point = new Point();

        point.charged_point = charged_point;
        point.used_point = used_point;
        point.total_point = total_point;
        point.user = user;

        return point;
    }
}
