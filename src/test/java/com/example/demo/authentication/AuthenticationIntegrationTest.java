package com.example.demo.authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl; // 追加
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 認証機能の統合テスト
 * 実際のSpring Securityの設定を使用してテストする
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("認証機能の統合テスト")
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("管理者ユーザーでログインできること")
    void 管理者ユーザーでログインできること() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("admin")
                .password("admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/home")) // redirectedUrl に変更
                .andExpect(authenticated()
                        .withUsername("admin")
                        .withRoles("ADMIN", "USER"));
    }

    @Test
    @DisplayName("一般ユーザーでログインできること")
    void 一般ユーザーでログインできること() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("user")
                .password("user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/home")) // redirectedUrl に変更
                .andExpect(authenticated()
                        .withUsername("user")
                        .withRoles("USER"));
    }

    @Test
    @DisplayName("無効なパスワードでログインできないこと")
    void 無効なパスワードでログインできないこと() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("admin")
                .password("wrong-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login?error*"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("存在しないユーザーでログインできないこと")
    void 存在しないユーザーでログインできないこと() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("nonexistent")
                .password("password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login?error*"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("空のユーザ名でログインできないこと")
    void 空のユーザ名でログインできないこと() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("")
                .password("password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login?error*"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("空のパスワードでログインできないこと")
    void 空のパスワードでログインできないこと() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("admin")
                .password(""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login?error*"))
                .andExpect(unauthenticated());
    }
}
