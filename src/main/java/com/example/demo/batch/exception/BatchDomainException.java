package com.example.demo.batch.exception;

/**
 * バッチドメイン例外
 * ビジネスロジック違反時に発生
 */
public class BatchDomainException extends RuntimeException {

    /**
     * エラーコード
     */
    private final BatchErrorCode errorCode;

    public BatchDomainException(BatchErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public BatchErrorCode getErrorCode() {
        return errorCode;
    }
}
