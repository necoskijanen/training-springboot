package com.example.demo.util;

/**
 * ページネーション計算ユーティリティ
 */
public class PaginationHelper {

    private PaginationHelper() {
        // ユーティリティクラスのインスタンス化を防止
    }

    /**
     * ページ番号とサイズからオフセットを計算する
     * 
     * @param page ページ番号（0から開始）
     * @param size ページサイズ
     * @return オフセット値
     */
    public static int calculateOffset(int page, int size) {
        return page * size;
    }

    /**
     * 総件数とサイズから総ページ数を計算する
     * 
     * @param totalCount 総件数
     * @param size       ページサイズ
     * @return 総ページ数
     */
    public static int calculateTotalPages(long totalCount, int size) {
        return (int) ((totalCount + size - 1) / size);
    }

    /**
     * 指定ページがより次ページが存在するかを判定する
     * 
     * @param currentPage 現在のページ番号
     * @param totalPages  総ページ数
     * @return 次ページが存在する場合 true、そうでない場合 false
     */
    public static boolean hasNextPage(int currentPage, int totalPages) {
        return currentPage < totalPages - 1;
    }

    /**
     * 指定ページが前ページが存在するかを判定する
     * 
     * @param currentPage 現在のページ番号
     * @return 前ページが存在する場合 true、そうでない場合 false
     */
    public static boolean hasPrevPage(int currentPage) {
        return currentPage > 0;
    }
}
