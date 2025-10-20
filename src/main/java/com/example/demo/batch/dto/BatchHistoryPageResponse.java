package com.example.demo.batch.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * バッチ履歴ページネーションレスポンスDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchHistoryPageResponse {
    private List<BatchHistoryResponse> content; // ページ内容
    private Long totalCount; // 全体件数
    private Integer totalPages; // 総ページ数
    private Integer currentPage; // 現在のページ番号（0から始まる）
    private Boolean hasNextPage; // 次ページが存在するか
    private Boolean hasPrevPage; // 前ページが存在するか
}
