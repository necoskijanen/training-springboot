package com.example.demo.application.batch.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * バッチ履歴検索条件DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchHistorySearchRequest {
    private String jobName; // バッチ名
    private String status; // ステータス
    private LocalDate startDateFrom; // 開始日時（開始）
    private LocalDate startDateTo; // 開始日時（終了）
    private LocalDate endDateFrom; // 終了日時（開始）
    private LocalDate endDateTo; // 終了日時（終了）
    private Long userId; // 実行ユーザーID（管理者用）
    private Integer page; // ページ番号（0から始まる）
    private Integer pageSize; // ページサイズ
}
