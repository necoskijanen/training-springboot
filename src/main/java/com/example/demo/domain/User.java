package com.example.demo.domain;

public record User(
        Long id,
        String name,
        String email,
        String password,
        Boolean isActive) {
}
