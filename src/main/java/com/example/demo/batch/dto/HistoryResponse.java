package com.example.demo.batch.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 履歴レスポンス（ページネーション対応）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryResponse {
    private List<HistoryItem> items;
    private int page;
    private int size;
    private long totalCount;
    private long totalPages;
}
