package com.example.demo.domain.user;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("AuthRole")
public class Role {
    private int id;
    private String name;

    public String getAuthority() {
        return "ROLE_" + name;
    }
}