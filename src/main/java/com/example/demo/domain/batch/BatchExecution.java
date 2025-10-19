package com.example.demo.domain.batch;

import com.example.demo.domain.batch.exception.BatchDomainException;
import com.example.demo.domain.batch.exception.BatchErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * バッチ実行履歴のドメインエンティティ
 * 状態遷移管理と検証ロジックを担当する
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchExecution {
    private String id; // UUID
    private String jobId; // ジョブID
    private String jobName; // ジョブ名
    private ExecutionStatus status; // ステータス（実行中、完了成功、失敗）
    private Integer exitCode; // 終了コード
    private Long userId; // ユーザーID
    private LocalDateTime startTime; // 開始時刻
    private LocalDateTime endTime; // 終了時刻
    private LocalDateTime createdAt; // 作成時刻

    /**
     * 新規バッチ実行を開始するファクトリメソッド
     * 
     * @param jobId   ジョブID
     * @param jobName ジョブ名
     * @param userId  ユーザーID
     * @return 実行中状態の新規バッチ実行オブジェクト
     */
    public static BatchExecution startNew(String jobId, String jobName, Long userId) {
        return BatchExecution.builder()
                .id(java.util.UUID.randomUUID().toString())
                .jobId(jobId)
                .jobName(jobName)
                .status(ExecutionStatus.RUNNING)
                .userId(userId)
                .startTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * バッチ実行を成功で完了させる
     * 
     * @throws BatchDomainException 実行中以外の状態で呼び出された場合
     */
    public void completeSuccessfully() {
        if (this.status != ExecutionStatus.RUNNING) {
            throw new BatchDomainException(BatchErrorCode.INVALID_STATUS_TRANSITION);
        }
        this.status = ExecutionStatus.COMPLETED_SUCCESS;
        this.exitCode = 0;
        this.endTime = LocalDateTime.now();
    }

    /**
     * バッチ実行を失敗で完了させる
     * 
     * @param exitCode 終了コード（0以外）
     * @throws BatchDomainException 実行中以外の状態で呼び出された場合、またはexitCodeが0の場合
     */
    public void completeFailed(int exitCode) {
        if (this.status != ExecutionStatus.RUNNING) {
            throw new BatchDomainException(BatchErrorCode.INVALID_STATUS_TRANSITION);
        }
        if (exitCode == 0) {
            throw new BatchDomainException(BatchErrorCode.BATCH_EXECUTION_FAILED);
        }
        this.status = ExecutionStatus.FAILED;
        this.exitCode = exitCode;
        this.endTime = LocalDateTime.now();
    }

    /**
     * バッチ実行をタイムアウトで失敗させる
     * 
     * @throws BatchDomainException 実行中以外の状態で呼び出された場合
     */
    public void timeout() {
        if (this.status != ExecutionStatus.RUNNING) {
            throw new BatchDomainException(BatchErrorCode.INVALID_STATUS_TRANSITION);
        }
        this.status = ExecutionStatus.FAILED;
        this.exitCode = -1; // タイムアウトを示す特殊な終了コード
        this.endTime = LocalDateTime.now();
    }

    /**
     * バッチ実行が実行中かどうかを判定する
     * 
     * @return 実行中の場合 true、そうでない場合 false
     */
    public boolean isRunning() {
        return this.status == ExecutionStatus.RUNNING;
    }

    /**
     * バッチ実行が完了しているかどうかを判定する
     * 
     * @return 完了している場合 true、そうでない場合 false
     */
    public boolean isCompleted() {
        return this.status != ExecutionStatus.RUNNING;
    }

    /**
     * バッチ実行が成功したかどうかを判定する
     * 
     * @return 成功の場合 true、そうでない場合 false
     */
    public boolean isSuccessful() {
        return this.status == ExecutionStatus.COMPLETED_SUCCESS;
    }
}
