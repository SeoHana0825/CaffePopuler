package com.example.caffepopularproject.domain.user.controller;

import com.example.caffepopularproject.domain.user.dto.request.LoginUserRequest;
import com.example.caffepopularproject.domain.user.dto.request.SaveUserRequest;
import com.example.caffepopularproject.domain.user.dto.response.LoginUserResponse;
import com.example.caffepopularproject.domain.user.dto.response.SaveUserResponse;
import com.example.caffepopularproject.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SaveUserResponse> signup (
            @RequestBody SaveUserRequest request
            ) {
        SaveUserResponse response = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login (
            @Valid @RequestBody LoginUserRequest request
            ) {
        LoginUserResponse response = userService.login(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout (
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        // 세션 무효화
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().build();
    }
}
