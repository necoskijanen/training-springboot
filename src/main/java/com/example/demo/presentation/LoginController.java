package com.example.demo.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        // src/main/resources/templates/login.html を表示
        return "login";
    }

}
