package com.example.demo.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
        return getAuthenticatedUsernameOptional().orElse(null);
    }

    /**
     * 認証済みユーザー名を取得（Optional版）
     * 
     * @return ユーザー名をラップしたOptional
     */
    public Optional<String> getAuthenticatedUsernameOptional() {
        if (!isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.of(getAuthentication().getName());
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

    /**
     * 認証済みユーザーのユーザー名を取得
     * 
     * @param authentication 認証情報
     * @return ユーザー名
     */
    public static String getCurrentUsername(Authentication authentication) {
        return getCurrentUsernameOptional(authentication).orElse(null);
    }

    /**
     * 認証済みユーザーのユーザー名を取得（Optional版）
     * 
     * @param authentication 認証情報
     * @return ユーザー名をラップしたOptional
     */
    public static Optional<String> getCurrentUsernameOptional(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.of(authentication.getName());
    }

    /**
     * 現在の認証済みユーザーのユーザーIDを取得
     * 
     * @return ユーザーID（未認証の場合はnull）
     */
    public Long getCurrentUserId() {
        return getCurrentUserIdOptional().orElse(null);
    }

    /**
     * 現在の認証済みユーザーのユーザーIDを取得（Optional版）
     * CustomUserDetails から直接取得（Repository依存なし）
     * 
     * @return ユーザーIDをラップしたOptional
     */
    public Optional<Long> getCurrentUserIdOptional() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return Optional.of(((CustomUserDetails) principal).getUserId());
        }
        return Optional.empty();
    }
}
