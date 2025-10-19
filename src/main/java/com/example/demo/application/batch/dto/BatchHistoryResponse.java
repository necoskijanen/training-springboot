package com.example.demo.application.batch.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * バッチ履歴レスポンスDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchHistoryResponse {
    private String executionId; // 実行ID（UUID）
    private String jobName; // ジョブ名
    private String status; // ステータス
    private Integer exitCode; // 終了コード
    private Long userId; // ユーザーID
    private String userName; // ユーザー名（管理者用に表示）
    private LocalDateTime startTime; // 開始時刻
    private LocalDateTime endTime; // 終了時刻
}
