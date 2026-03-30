package com.example.caffepopularproject.domain.user.dto.response;

import com.example.caffepopularproject.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class SaveUserResponse {


    private final Long id;
    private final String nickname;
    private final String email;
    private final String phoneNo;

    public static SaveUserResponse from (User user) {
        return new SaveUserResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getPhoneNo()
        );
    }

}
