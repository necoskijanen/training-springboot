package com.example.demo.authentication.presentation;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // 認証されたユーザーが持つ権限（ロール）を取得
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 💡 ロールに基づいてリダイレクト先を決定
        String redirectUrl = "/"; // デフォルトのリダイレクト先

        // ロールをチェック
        // ADMINロールを持つか確認 (ADMINはUSERも兼ねることが多いが、ADMINを優先)
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            redirectUrl = "/admin/home";
        }
        // ADMINロールを持たず、USERロールを持つか確認
        else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            redirectUrl = "/user/home";
        }

        // 決定したURLへリダイレクト
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}