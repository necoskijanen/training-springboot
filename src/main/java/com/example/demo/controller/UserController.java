package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user") // ベースパス
public class UserController {

    @GetMapping("/home") // 👈 ログイン後にリダイレクトされるべきマッピング
    public String userHome() {
        // src/main/resources/templates/user/home.html を返す
        return "user/home";
    }
}
