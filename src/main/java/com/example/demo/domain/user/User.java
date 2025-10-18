package com.example.demo.domain.user;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Alias("AuthUser")
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Boolean isActive;
    private Boolean admin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Role> roles;
}