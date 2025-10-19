package com.example.demo.util;

/**
 * 文字列ユーティリティクラス
 * null、空白文字列の判定とデフォルト値取得を提供
 */
public final class StringUtil {

    private StringUtil() {
        // ユーティリティクラスのため、インスタンス化を防止
    }

    /**
     * 文字列がnullまたは空白（スペースを含む）かどうかを判定
     *
     * @param str 対象文字列
     * @return nullまたは空白の場合true
     */
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    /**
     * 文字列がnullまたは空白（スペースを含む）でないかどうかを判定
     *
     * @param str 対象文字列
     * @return nullまたは空白でない場合true
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 文字列がnullまたは空の場合、デフォルト値を返す
     *
     * @param str          対象文字列
     * @param defaultValue デフォルト値
     * @return 文字列がnullまたは空の場合はdefaultValue、そうでなければstr
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return isBlank(str) ? defaultValue : str;
    }

    /**
     * 文字列をトリム後、空かどうかを判定
     *
     * @param str 対象文字列
     * @return トリム後が空の場合true
     */
    public static boolean isTrimmedEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 文字列をトリム後、空でないかどうかを判定
     *
     * @param str 対象文字列
     * @return トリム後が空でない場合true
     */
    public static boolean isTrimmedNotEmpty(String str) {
        return !isTrimmedEmpty(str);
    }
}
