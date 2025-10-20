package com.example.demo.batch.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * バッチ履歴検索条件DTO
 * クエリパラメータから受け取る検索条件を表現
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BatchHistorySearchRequest {
    private String jobName; // ジョブ名（部分一致、optional）
    private String status; // ステータス（optional）
    private LocalDateTime startDateFrom; // 開始日時の下限（optional）
    private LocalDateTime endDateTo; // 終了日時の上限（optional）
    private String userName; // 検索対象ユーザー名（管理者用、optional）

    @Builder.Default
    private Integer page = 0; // ページ番号（0から始まる、デフォルト0）

    @Builder.Default
    private Integer pageSize = 10; // ページサイズ（デフォルト10）
}
