package com.example.demo.authentication.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import java.util.List;

@Data
@Alias("AuthUser")
public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private Boolean isActive;
    private List<Role> roles;
}