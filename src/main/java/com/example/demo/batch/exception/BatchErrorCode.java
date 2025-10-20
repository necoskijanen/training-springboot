package com.example.demo.batch.exception;

/**
 * バッチ処理関連のエラーコード
 */
public enum BatchErrorCode {
    INVALID_JOB_ID("batch.error.invalid.job.id"),
    JOB_NOT_FOUND("batch.error.job.not.found"),
    BATCH_NOT_FOUND("batch.error.batch.not.found"),
    BATCH_TIMEOUT("batch.error.batch.timeout"),
    BATCH_EXECUTION_FAILED("batch.error.batch.execution.failed"),
    INVALID_STATUS_TRANSITION("batch.error.invalid.status.transition");

    private final String messageKey;

    BatchErrorCode(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
