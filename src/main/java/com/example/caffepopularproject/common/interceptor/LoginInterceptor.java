package com.example.caffepopularproject.common.interceptor;

import com.example.caffepopularproject.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        // session 이 없거나 session에 회원정보가 없을 경우
        if (session == null || session.getAttribute("LOGIN_USER_ID") == null) {

            // 로그인 페이지로 보냄
            response.sendRedirect(ErrorCode.USER_NOT_FOUND.getMessage());

            // 컨트롤러 수행 안함
            return false;
        }
        return true;
    }
}
