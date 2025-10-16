package com.example.demo.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("Role")
public class Role {
    private int id;
    private String name;
}