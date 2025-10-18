package com.example.demo.authentication.presentation;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        // 例外の種類によってメッセージキーを決定
        final String errorMessageKey = switch (exception) {
            case BadCredentialsException e -> "login.error.badCredentials";
            default -> "login.error";
        };

        // セッションにメッセージキーを保存
        request.getSession().setAttribute("errorMessageKey", errorMessageKey);

        // エラーパラメータはシンプルにtrueだけ
        setDefaultFailureUrl("/login?error=true");
        super.onAuthenticationFailure(request, response, exception);
    }
}
