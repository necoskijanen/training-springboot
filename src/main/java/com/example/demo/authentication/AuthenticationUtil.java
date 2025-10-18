package com.example.demo.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 認証情報ユーティリティ
 */
@Component
public class AuthenticationUtil {

    private static final String ANONYMOUS_USER = "anonymousUser";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    /**
     * 現在の認証情報を取得
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * ログイン済みか判定
     */
    public boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !ANONYMOUS_USER.equals(authentication.getPrincipal());
    }

    /**
     * ADMIN ロールを持っているか判定
     */
    public boolean hasAdminRole() {
        return hasRole(ROLE_ADMIN);
    }

    /**
     * USER ロールを持っているか判定
     */
    public boolean hasUserRole() {
        return hasRole(ROLE_USER);
    }

    /**
     * 指定されたロールを持っているか判定
     * 
     * @param role ロール名（例：ROLE_ADMIN）
     * @return ロールを持つ場合true
     */
    public boolean hasRole(String role) {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    /**
     * 認証済みユーザー名を取得
     * 
     * @return ユーザー名（未認証の場合はnull）
     */
    public String getAuthenticatedUsername() {
        if (!isAuthenticated()) {
            return null;
        }
        return getAuthentication().getName();
    }

    /**
     * 認証済みユーザーが複数のロールを持つか判定
     * 
     * @param roles ロール名の可変長引数
     * @return すべてのロールを持つ場合true
     */
    public boolean hasAllRoles(String... roles) {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        for (String role : roles) {
            if (!hasRole(role)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 認証済みユーザーが複数のロールのいずれかを持つか判定
     * 
     * @param roles ロール名の可変長引数
     * @return いずれかのロールを持つ場合true
     */
    public boolean hasAnyRole(String... roles) {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }
}
