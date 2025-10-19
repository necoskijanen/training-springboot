package com.example.demo.application.batch.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * バッチ検索パラメータ
 * Application層 → Domain層（Repository）への検索条件を表現
 * DTO（Presentation層）から独立した内部使用オブジェクト
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchSearchParams {
    private Long userId; // 検索対象ユーザーID（null = 全員）
    private String jobName; // ジョブ名（部分一致、optional）
    private String status; // ステータス（optional）
    private LocalDateTime startDateFrom; // 開始日時の下限（optional）
    private LocalDateTime endDateTo; // 終了日時の上限（optional）
    private Integer offset; // ページング用オフセット
    private Integer pageSize; // ページサイズ

    /**
     * BatchHistorySearchRequest から BatchSearchParams を生成する
     * 
     * @param request 検索条件DTO
     * @param userId  検索対象ユーザーID（アクセス制御後）
     * @return 検索パラメータオブジェクト
     */
    public static BatchSearchParams of(BatchHistorySearchRequest request, Long userId, int offset) {
        return BatchSearchParams.builder()
                .userId(userId)
                .jobName(request.getJobName())
                .status(request.getStatus())
                .startDateFrom(request.getStartDateFrom())
                .endDateTo(request.getEndDateTo())
                .offset(offset)
                .pageSize(request.getPageSize())
                .build();
    }
}
