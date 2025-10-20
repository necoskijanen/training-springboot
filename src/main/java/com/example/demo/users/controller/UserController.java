package com.example.demo.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.security.AuthenticationUtil;

@Controller
@RequestMapping("/user") // ベースパス
public class UserController {

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @GetMapping("/home") // 👈 ログイン後にリダイレクトされるべきマッピング
    public String userHome(Model model) {
        // src/main/resources/templates/user/home.html を返す
        model.addAttribute("username", authenticationUtil.getAuthenticatedUsername());
        return "user/home";
    }

    @GetMapping("/batch/start")
    public String userBatchStart() {
        return "batch/start";
    }

    @GetMapping("/batch/history")
    public String userBatchHistory() {
        return "batch/history";
    }

}
