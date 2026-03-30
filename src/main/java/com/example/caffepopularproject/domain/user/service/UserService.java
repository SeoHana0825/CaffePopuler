package com.example.caffepopularproject.domain.user.service;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.user.dto.request.LoginUserRequest;
import com.example.caffepopularproject.domain.user.dto.request.SaveUserRequest;
import com.example.caffepopularproject.domain.user.dto.response.LoginUserResponse;
import com.example.caffepopularproject.domain.user.dto.response.SaveUserResponse;
import com.example.caffepopularproject.domain.user.entity.User;
import com.example.caffepopularproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Service
@RequiredArgsConstructor
@RestControllerAdvice
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    /* 회원 API
     * 1. 회원가입
     * 2. 로그인
     * 3. 로그아웃
     */

    @Transactional
    public SaveUserResponse signup (SaveUserRequest request) {
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new ServiceException(ErrorCode.INVALID_PASSWORD);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ServiceException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        String encodePassword = passwordEncoder.encode(request.getPassword());
        User user = User.register(request, encodePassword);
        userRepository.save(user);

        return SaveUserResponse.from(user);
    }

    @Transactional (readOnly = true)
    public LoginUserResponse login (LoginUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(),user.getPassword())) {
            throw new ServiceException(ErrorCode.INVALID_PASSWORD);
        }
        return LoginUserResponse.from(user);
    }
}
