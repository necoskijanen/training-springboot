package com.example.demo.users.exception;

/**
 * ユーザドメイン例外
 * ビジネスロジック違反時に発生
 */
public class UserDomainException extends RuntimeException {

    /**
     * エラーコード
     */
    private final UserErrorCode errorCode;

    public UserDomainException(UserErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public UserErrorCode getErrorCode() {
        return errorCode;
    }
}