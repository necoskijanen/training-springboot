package com.example.demo.presentation;

import com.example.demo.authentication.AuthenticationUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @GetMapping("/login")
    public String login() {
        // ログイン済みユーザーをリダイレクト
        if (authenticationUtil.isAuthenticated()) {
            if (authenticationUtil.hasAdminRole()) {
                return "redirect:/admin/home";
            } else if (authenticationUtil.hasUserRole()) {
                return "redirect:/user/home";
            }
        }
        return "login";
    }

    @GetMapping("/")
    public String root() {
        // ログイン状態を確認
        if (!authenticationUtil.isAuthenticated()) {
            return "redirect:/login";
        }

        // ロールに応じてリダイレクト
        if (authenticationUtil.hasAdminRole()) {
            return "redirect:/admin/home";
        } else if (authenticationUtil.hasUserRole()) {
            return "redirect:/user/home";
        }

        return "redirect:/login";
    }
}
