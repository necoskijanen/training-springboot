package com.example.demo.domain.user;

import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * ロールのドメインエンティティ
 * ロール情報と権限検査ロジックを担当する
 */
@Data
@Alias("AuthRole")
public class Role {
    private int id;
    private String name;

    /**
     * Spring Security形式の権限文字列を取得する
     * 
     * @return "ROLE_"プレフィックス付きのロール名
     */
    public String getAuthority() {
        return "ROLE_" + name;
    }

    /**
     * このロールが管理者ロールであるかを判定する
     * 
     * @return 管理者ロールの場合 true、そうでない場合 false
     */
    public boolean isAdmin() {
        return "ADMIN".equals(this.name);
    }

    /**
     * このロールが一般ユーザロールであるかを判定する
     * 
     * @return 一般ユーザロールの場合 true、そうでない場合 false
     */
    public boolean isUser() {
        return "USER".equals(this.name);
    }

    /**
     * このロールがユーザ管理権限を持つかを判定する
     * 
     * @return ユーザ管理権限を持つ場合 true、そうでない場合 false
     */
    public boolean canManageUsers() {
        return isAdmin();
    }

    /**
     * このロールがバッチ履歴表示権限を持つかを判定する
     * 
     * @return バッチ履歴表示権限を持つ場合 true、そうでない場合 false
     */
    public boolean canViewBatchHistory() {
        return true; // 全ロールが可能
    }

    /**
     * このロールがバッチ実行権限を持つかを判定する
     * 
     * @return バッチ実行権限を持つ場合 true、そうでない場合 false
     */
    public boolean canExecuteBatch() {
        return true; // 全ロールが可能
    }
}
