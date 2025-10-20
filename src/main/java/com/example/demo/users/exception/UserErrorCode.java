package com.example.demo.users.exception;

/**
 * ユーザ関連のエラーコード
 */
public enum UserErrorCode {
    DUPLICATE_NAME("user.error.duplicate.name"),
    USER_NOT_FOUND("user.error.not.found"),
    CANNOT_REVOKE_OWN_ADMIN_RIGHTS("user.error.cannot.revoke.own.admin.rights");

    private final String messageKey;

    UserErrorCode(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}