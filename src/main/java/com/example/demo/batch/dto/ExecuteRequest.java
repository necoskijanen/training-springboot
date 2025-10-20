package com.example.demo.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * バッチ実行リクエスト
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExecuteRequest {
    private String jobId;
}
