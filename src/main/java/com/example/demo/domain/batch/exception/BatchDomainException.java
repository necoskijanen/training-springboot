package com.example.demo.domain.batch.exception;

/**
 * バッチドメインの例外
 * 不正な状態遷移や検証エラーなど、ビジネスルール違反を表現する
 */
public class BatchDomainException extends RuntimeException {

    /**
     * メッセージ付きで例外を構築する
     * 
     * @param message エラーメッセージ
     */
    public BatchDomainException(String message) {
        super(message);
    }

    /**
     * メッセージと原因付きで例外を構築する
     * 
     * @param message エラーメッセージ
     * @param cause   原因となった例外
     */
    public BatchDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
