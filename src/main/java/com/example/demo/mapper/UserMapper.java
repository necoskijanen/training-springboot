package com.example.demo.mapper;

import com.example.demo.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;
import java.util.List;

@Mapper
public interface UserMapper {

    Optional<User> findByEmail(@Param("email") String email);

    List<String> findRolesByUserId(@Param("userId") Long userId);
}
