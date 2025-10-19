package com.example.demo.application.user.mapper;

import org.mapstruct.Mapper;

import com.example.demo.application.user.dto.UpdateUserRequest;
import com.example.demo.application.user.dto.UserResponse;
import com.example.demo.domain.user.User;

/**
 * ユーザ Entity と DTO 間の変換を担当する Mapper
 * MapStruct により実装が自動生成される
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * User Entity を UserResponse DTO に変換する
     * 
     * @param user ユーザエンティティ
     * @return ユーザレスポンス DTO
     */
    UserResponse toUserResponse(User user);

    /**
     * User Entity を UpdateUserRequest DTO に変換する
     * 
     * @param user ユーザエンティティ
     * @return ユーザ更新リクエスト DTO
     */
    UpdateUserRequest toUpdateUserRequest(User user);

    /**
     * User Entity リストを UserResponse DTO リストに変換する
     * 
     * @param users ユーザエンティティのリスト
     * @return ユーザレスポンス DTO のリスト
     */
    java.util.List<UserResponse> toUserResponseList(java.util.List<User> users);
}
