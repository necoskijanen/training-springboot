package com.example.demo.domain.user.repository;

import com.example.demo.domain.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * ユーザリポジトリ（MyBatisマッパー）
 */
@Mapper
public interface UserRepository {

    /**
     * ユーザ名でユーザを検索
     * 
     * @param name ユーザ名
     * @return ユーザ
     */
    Optional<User> findByName(@Param("name") String name);

    /**
     * IDでユーザを検索
     * 
     * @param id ユーザID
     * @return ユーザ
     */
    Optional<User> findById(@Param("id") Long id);

    /**
     * ユーザ名またはメールアドレスで検索
     * 
     * @param name  ユーザ名（部分一致、nullの場合は検索条件に含めない）
     * @param email メールアドレス（部分一致、nullの場合は検索条件に含めない）
     * @return ユーザリスト
     */
    List<User> searchUsers(@Param("name") String name, @Param("email") String email);

    /**
     * ユーザ名が既に存在するか確認
     * 
     * @param name ユーザ名
     * @return 存在する場合true
     */
    boolean existsByName(@Param("name") String name);

    /**
     * ユーザを新規作成
     * 
     * @param user ユーザ情報
     */
    void insert(User user);

    /**
     * ユーザを更新
     * 
     * @param user ユーザ情報
     */
    void update(User user);

    /**
     * ユーザを削除
     * 
     * @param id ユーザID
     */
    void deleteById(@Param("id") Long id);

    /**
     * 全ユーザを取得
     * 
     * @return ユーザリスト
     */
    List<User> findAll();
}