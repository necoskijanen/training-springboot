package com.example.demo.batch.dto;

/**
 * バッチ実行ステータスの定義
 */
public enum ExecutionStatus {
    RUNNING("実行中"),
    COMPLETED_SUCCESS("完了（成功）"),
    FAILED("失敗");

    private final String displayName;

    ExecutionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ExecutionStatus fromValue(String value) {
        try {
            return ExecutionStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return RUNNING;
        }
    }
}
