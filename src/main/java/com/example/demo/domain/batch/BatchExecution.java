package com.example.demo.domain.batch;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * バッチ実行履歴のドメインエンティティ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchExecution {
    private String id; // UUID
    private String jobId; // ジョブID
    private String jobName; // ジョブ名
    private String status; // ステータス（RUNNING, COMPLETED_SUCCESS, FAILED）
    private Integer exitCode; // 終了コード
    private Long userId; // ユーザーID
    private LocalDateTime startTime; // 開始時刻
    private LocalDateTime endTime; // 終了時刻
    private LocalDateTime createdAt; // 作成時刻
}
