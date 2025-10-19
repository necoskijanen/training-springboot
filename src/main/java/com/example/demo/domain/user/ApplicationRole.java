package com.example.demo.domain.user;

/**
 * アプリケーションで使用するロール定義
 * Spring Security の権限文字列（ROLE_* プレフィックス付き）を管理する
 */
public enum ApplicationRole {
    ADMIN("ROLE_ADMIN", "管理者"),
    USER("ROLE_USER", "一般ユーザー");

    private final String authority;
    private final String displayName;

    /**
     * ロールを初期化する
     * 
     * @param authority   Spring Security の権限文字列（ROLE_ プレフィックス付き）
     * @param displayName ロールの表示名
     */
    ApplicationRole(String authority, String displayName) {
        this.authority = authority;
        this.displayName = displayName;
    }

    /**
     * Spring Security の権限文字列を取得する
     * 
     * @return 権限文字列（例："ROLE_ADMIN"）
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * ロールの表示名を取得する
     * 
     * @return 表示名
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 権限文字列からロールを検索する
     * 
     * @param authority 権限文字列
     * @return マッチするロール、見つからない場合は null
     */
    public static ApplicationRole fromAuthority(String authority) {
        for (ApplicationRole role : values()) {
            if (role.authority.equals(authority)) {
                return role;
            }
        }
        return null;
    }
}
