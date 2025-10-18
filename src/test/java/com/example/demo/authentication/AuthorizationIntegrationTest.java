package com.example.demo.authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 認可（アクセス制御）の統合テスト
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("認可機能の統合テスト")
class AuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("未認証ユーザーは管理者ページにアクセスできないこと")
    void 未認証ユーザーは管理者ページにアクセスできないこと() throws Exception {
        mockMvc.perform(get("/admin/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("未認証ユーザーは一般ユーザーページにアクセスできないこと")
    void 未認証ユーザーは一般ユーザーページにアクセスできないこと() throws Exception {
        mockMvc.perform(get("/user/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("ログインページは未認証でもアクセスできること")
    void ログインページは未認証でもアクセスできること() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = { "ADMIN", "USER" })
    @DisplayName("管理者は管理者ページにアクセスできること")
    void 管理者は管理者ページにアクセスできること() throws Exception {
        mockMvc.perform(get("/admin/home"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = { "ADMIN", "USER" })
    @DisplayName("管理者は一般ユーザーページにもアクセスできること")
    void 管理者は一般ユーザーページにもアクセスできること() throws Exception {
        mockMvc.perform(get("/user/home"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = { "USER" })
    @DisplayName("一般ユーザーは一般ユーザーページにアクセスできること")
    void 一般ユーザーは一般ユーザーページにアクセスできること() throws Exception {
        mockMvc.perform(get("/user/home"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = { "USER" })
    @DisplayName("一般ユーザーは管理者ページにアクセスできないこと")
    void 一般ユーザーは管理者ページにアクセスできないこと() throws Exception {
        mockMvc.perform(get("/admin/home"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = { "ADMIN", "USER" })
    @DisplayName("管理者はCSSファイルにアクセスできること")
    void 管理者はCSSファイルにアクセスできること() throws Exception {
        mockMvc.perform(get("/css/common.css"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("未認証ユーザーもCSSファイルにアクセスできること")
    void 未認証ユーザーもCSSファイルにアクセスできること() throws Exception {
        mockMvc.perform(get("/css/common.css"))
                .andExpect(status().isOk());
    }
}
