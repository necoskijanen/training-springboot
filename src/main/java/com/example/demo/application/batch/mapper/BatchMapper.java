package com.example.demo.application.batch.mapper;

import com.example.demo.application.batch.dto.BatchHistoryResponse;
import com.example.demo.presentation.BatchRestController.HistoryItem;
import com.example.demo.presentation.BatchRestController.StatusResponse;
import com.example.demo.domain.batch.BatchExecution;

/**
 * バッチ処理の Entity と DTO 間の変換を担当する Mapper
 */
public class BatchMapper {

    private BatchMapper() {
        // ユーティリティクラスのインスタンス化を防止
    }

    /**
     * BatchExecution を StatusResponse に変換する
     * 
     * @param execution バッチ実行エンティティ
     * @return ステータスレスポンス
     */
    public static StatusResponse toStatusResponse(BatchExecution execution) {
        if (execution == null) {
            return null;
        }
        return StatusResponse.builder()
                .status(execution.getStatus())
                .exitCode(execution.getExitCode())
                .startTime(execution.getStartTime())
                .endTime(execution.getEndTime())
                .jobName(execution.getJobName())
                .build();
    }

    /**
     * BatchExecution を HistoryItem に変換する
     * 
     * @param execution バッチ実行エンティティ
     * @return 履歴アイテム
     */
    public static HistoryItem toHistoryItem(BatchExecution execution) {
        if (execution == null) {
            return null;
        }
        return HistoryItem.builder()
                .id(execution.getId())
                .jobId(execution.getJobId())
                .jobName(execution.getJobName())
                .status(execution.getStatus())
                .startTime(execution.getStartTime())
                .endTime(execution.getEndTime())
                .exitCode(execution.getExitCode())
                .build();
    }

    /**
     * BatchExecution を BatchHistoryResponse に変換する
     * 
     * @param execution バッチ実行エンティティ
     * @param userName  ユーザー名（管理者用）
     * @param userId    ユーザーID
     * @return バッチ履歴レスポンス
     */
    public static BatchHistoryResponse toHistoryResponse(
            BatchExecution execution,
            String userName,
            Long userId) {
        if (execution == null) {
            return null;
        }
        return BatchHistoryResponse.builder()
                .executionId(execution.getId())
                .jobName(execution.getJobName())
                .status(execution.getStatus())
                .exitCode(execution.getExitCode())
                .userId(userId)
                .userName(userName)
                .startTime(execution.getStartTime())
                .endTime(execution.getEndTime())
                .build();
    }
}
