package com.example.demo.domain.user;

import com.example.demo.domain.user.exception.UserDomainException;
import com.example.demo.domain.user.exception.UserErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * ユーザのドメインエンティティ
 * ビジネスロジックと状態管理を担当する Rich Domain Model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private List<Role> roles = List.of();

    /**
     * ユーザの基本情報を更新する
     * 
     * @param name  新しい名前
     * @param email 新しいメールアドレス
     * @param admin 管理者フラグ
     * @throws UserDomainException 自分自身の管理者権限を削除しようとした場合
     */
    public void updateUserInfo(String name, String email, Boolean admin, Long updaterId) {
        // 管理者が自身の権限を削除しようとしていないか確認
        if (hasAdminRole() && !admin && Objects.equals(id, updaterId)) {
            throw new UserDomainException(UserErrorCode.CANNOT_REVOKE_OWN_ADMIN_RIGHTS);
        }

        this.name = name;
        this.email = email;
        this.admin = admin;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * ユーザが有効かどうかを判定する
     * 
     * @return ユーザが有効な場合 true、そうでない場合 false
     */
    public boolean isActive() {
        return this.isActive != null && this.isActive;
    }

    /**
     * ユーザが管理者ロールを持つかどうかを判定する
     * 
     * @return ユーザが管理者である場合 true、そうでない場合 false
     */
    public boolean hasAdminRole() {
        return this.admin != null && this.admin;
    }

    /**
     * ユーザが特定のロール名を持つか判定する
     * 
     * @param roleName ロール名（例："ADMIN", "USER"）
     * @return ユーザが指定されたロール名を持つ場合 true、そうでない場合 false
     */
    public boolean hasRole(String roleName) {
        return this.roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    /**
     * パスワードが有効かどうかを判定する（エンコード済みパスワードと照合）
     * 
     * @param rawPassword     生パスワード
     * @param passwordEncoder パスワードエンコーダ
     * @return パスワードが正しい場合 true、そうでない場合 false
     */
    public boolean isPasswordValid(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.password);
    }

    /**
     * 新規ユーザを作成するファクトリメソッド
     * 
     * @param name            ユーザ名
     * @param email           メールアドレス
     * @param encodedPassword エンコード済みパスワード
     * @param isAdmin         管理者フラグ
     * @return 新規ユーザオブジェクト
     */
    public static User createNewUser(String name, String email, String encodedPassword, Boolean isAdmin) {
        return User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .isActive(true)
                .admin(isAdmin != null && isAdmin)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
