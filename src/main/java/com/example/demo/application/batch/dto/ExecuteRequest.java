package com.example.demo.application.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * バッチ実行リクエスト
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteRequest {
    private String jobId;
}
