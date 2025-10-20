package com.example.demo.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * バッチ実行レスポンス
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecuteResponse {
    private String executionId;
    private String error;

    public ExecuteResponse(String executionId) {
        this.executionId = executionId;
        this.error = null;
    }
}
