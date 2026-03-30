package com.example.caffepopularproject.domain.user.service;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.user.dto.request.LoginUserRequest;
import com.example.caffepopularproject.domain.user.dto.request.SaveUserRequest;
import com.example.caffepopularproject.domain.user.dto.response.LoginUserResponse;
import com.example.caffepopularproject.domain.user.dto.response.SaveUserResponse;
import com.example.caffepopularproject.domain.user.entity.User;
import com.example.caffepopularproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() {

        // given
        SaveUserRequest request = new SaveUserRequest(
                "제시카 알바몬",
                "test@test.com",
                "010-0000-0000",
                "12345678"
        );
        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("encoded_password");

        // when
        SaveUserResponse response = userService.signup(request);

        // then
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 - 비밀번호 8자이상 조건 미달 예외 발생")
    void signup_Fail_UnderEight() {

        // given
        SaveUserRequest request = new SaveUserRequest(
                "제시카 알바몬",
                "test@test.com",
                "010-0000-0000",
                "12345"
        );

        // when&then
        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("회원가입 - 중복 이메일 예외 발생")
    void signup_Fail_DuplicateEmail() {

        // given
        SaveUserRequest request = new SaveUserRequest(
                "제시카 알바몬",
                "test@test.com",
                "010-0000-0000",
                "12345678"
        );
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when&then
        assertThatThrownBy(() ->  userService.signup(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {

        // given
        LoginUserRequest request = new LoginUserRequest(
                "test@test.com",
                "12345678"
        );

        SaveUserRequest dummyRequest = new SaveUserRequest(
                "제시카 알바몬",
                "test@test.com",
                "010-0000-0000",
                "12345678"
        );

        User mockUser = User.register(dummyRequest, "encoded_password");

        given(userRepository.findByEmail(request.getEmail())).willReturn(java.util.Optional.of(mockUser));
        given(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).willReturn(true);

        // when
        LoginUserResponse response = userService.login(request);

        // then
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_Fail_UserNotFound() {

        // given
        LoginUserRequest request = new LoginUserRequest(
                "test@test.com",
                "12345678"
        );

        given(userRepository.findByEmail(request.getEmail())).willReturn(java.util.Optional.empty());

        // when&then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_Fail_InvalidPassword() {
        LoginUserRequest request = new LoginUserRequest(
                "test@test.com",
                "12345678"
        );

        SaveUserRequest dummyRequest = new SaveUserRequest(
                "제시카 알바몬",
                "test@test.com",
                "010-0000-0000",
                "12345678"
        );

        User mockUser = User.register(dummyRequest, "encoded_password");

        given(userRepository.findByEmail(request.getEmail())).willReturn(java.util.Optional.of(mockUser));
        given(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).willReturn(false);

        // when&then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.INVALID_PASSWORD.getMessage());
   }
}
