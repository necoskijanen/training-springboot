package com.example.demo.batch;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.demo.authentication.CustomUserDetails;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * バッチ処理の結合テスト
 * 
 * テストシナリオ：
 * 1. 一般ユーザでバッチジョブ(wait_time_test)を実行
 * 2. 実行完了を確認
 * 3. 管理者でバッチ履歴を確認し、ジョブが正常終了していることを確認
 */
@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("バッチ処理の結合テスト")
public class BatchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_JOB_ID = "wait_time_test";

    @Test
    @DisplayName("一般ユーザが実行したバッチジョブを管理者が履歴確認できること")
    public void testBatchExecutionAndHistoryConfirmation() throws Exception {
        // テスト用のユーザー詳細を作成
        CustomUserDetails userDetails = createUserDetails(2L, "user", "ROLE_USER");
        CustomUserDetails adminDetails = createUserDetails(1L, "admin", "ROLE_ADMIN");

        // 1. 一般ユーザでバッチジョブを実行
        String executeRequest = "{\"jobId\":\"" + TEST_JOB_ID + "\"}";
        MvcResult executeResult = mockMvc.perform(post("/api/batch/execute")
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(executeRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionId", notNullValue()))
                .andReturn();

        // 実行IDを抽出
        String responseBody = executeResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String executionId = jsonNode.get("executionId").asText();

        // 2. ステータスポーリング（完了を確認）
        waitForBatchCompletion(executionId, userDetails);

        // 3. 管理者で履歴確認し、実行したジョブが正常終了していることを確認
        mockMvc.perform(get("/api/batch/history/search?page=0&size=10")
                .with(user(adminDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].status", equalTo("COMPLETED_SUCCESS")))
                .andExpect(jsonPath("$.content[0].jobName", equalTo("待機テスト（成功）")));
    }

    /**
     * CustomUserDetailsオブジェクトを作成するヘルパーメソッド
     * 
     * @param userId   ユーザーID
     * @param username ユーザー名
     * @param role     ロール（例：ROLE_USER, ROLE_ADMIN）
     * @return CustomUserDetails インスタンス
     */
    private CustomUserDetails createUserDetails(Long userId, String username, String role) {
        return new CustomUserDetails(
                userId,
                username,
                "password",
                Arrays.asList(new SimpleGrantedAuthority(role)),
                true);
    }

    /**
     * バッチ処理の完了を待つ
     * 
     * @param executionId 実行ID
     * @param userDetails ユーザー詳細
     * @throws Exception
     */
    private void waitForBatchCompletion(String executionId, CustomUserDetails userDetails) throws Exception {
        int maxRetries = 5;
        int intervalMillis = 1000;
        int retryCount = 0;
        String status;

        while (retryCount < maxRetries) {
            MvcResult statusResult = mockMvc.perform(get("/api/batch/status/" + executionId)
                    .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = statusResult.getResponse().getContentAsString();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            status = jsonNode.get("status").asText();

            if ("COMPLETED_SUCCESS".equals(status) || "FAILED".equals(status)) {
                // ジョブが完了した
                break;
            }

            // 100ms待機
            Thread.sleep(intervalMillis);
            retryCount++;
        }
    }
}
