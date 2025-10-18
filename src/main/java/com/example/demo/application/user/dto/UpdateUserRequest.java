package com.example.demo.application.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotNull(message = "ユーザーIDは必須です")
    private Long id;

    @NotBlank(message = "ユーザー名は必須です")
    private String name;

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;

    @NotNull(message = "管理者権限の設定は必須です")
    private Boolean admin;
}
