package com.example.demo.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("RoleDefinition")
public class Role {
    private int id;
    private String name;

    public String getAuthName() {
        return "ROLE_" + name;
    }
}