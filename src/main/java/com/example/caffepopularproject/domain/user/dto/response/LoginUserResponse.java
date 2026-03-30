package com.example.caffepopularproject.domain.user.dto.response;

import com.example.caffepopularproject.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginUserResponse {

    private final Long id;
    private final String email;
    private final String password;

    public static LoginUserResponse from (User user) {
        return new LoginUserResponse(user.getId(), user.getEmail(), user.getPassword());
    }
}
