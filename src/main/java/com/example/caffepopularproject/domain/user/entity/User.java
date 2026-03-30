package com.example.caffepopularproject.domain.user.entity;

import com.example.caffepopularproject.domain.user.dto.request.SaveUserRequest;
import com.example.caffepopularproject.domain.user.dto.response.SaveUserResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table (name = "user")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String nickname;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String phoneNo;

    @Column(nullable = false, length = 100)
    private String password;

    public static User register (SaveUserRequest request, String encodePassword) {
        User user = new User();
        user.nickname = request.getNickname();
        user.email = request.getEmail();
        user.phoneNo = request.getPhoneNo();
        user.password = encodePassword;
        return user;
    }
}
