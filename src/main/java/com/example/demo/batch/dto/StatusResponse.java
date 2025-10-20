package com.example.demo.batch.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ステータスレスポンス
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusResponse {
    private ExecutionStatus status;
    private Integer exitCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String jobName;
}
