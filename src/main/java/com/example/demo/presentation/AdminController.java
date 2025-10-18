package com.example.demo.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin") // ベースパス
public class AdminController {

    @GetMapping("/home") // 👈 ログイン後にリダイレクトされるべきマッピング
    public String adminHome() {
        // src/main/resources/templates/admin/home.html を返す
        return "admin/home";
    }
}
