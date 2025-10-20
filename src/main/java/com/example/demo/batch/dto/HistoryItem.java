package com.example.demo.batch.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 履歴アイテム
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryItem {
    private String id;
    private String jobId;
    private String jobName;
    private ExecutionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer exitCode;
}
