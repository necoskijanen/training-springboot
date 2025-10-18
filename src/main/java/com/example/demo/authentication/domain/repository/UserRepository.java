package com.example.demo.authentication.domain.repository;

import com.example.demo.authentication.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * ユーザリポジトリ（MyBatisマッパー）
 */
@Mapper
public interface UserRepository {
    /**
     * メールアドレスでユーザを検索
     * 
     * @param email メールアドレス
     * @return ユーザ
     */
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * ユーザ名でユーザを検索
     * 
     * @param name ユーザ名
     * @return ユーザ
     */
    Optional<User> findByName(@Param("name") String name);
}