package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MvcController {

    @GetMapping("/login")
    public String login() {
        // src/main/resources/templates/login.html を表示
        return "login";
    }

    @GetMapping("/")
    public String home() {
        // src/main/resources/templates/home.html を表示
        return "home";
    }
}
