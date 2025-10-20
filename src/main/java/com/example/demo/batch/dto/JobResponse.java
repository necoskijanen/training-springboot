package com.example.demo.batch.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ジョブのDTOレスポンス
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {
    private String id;
    private String name;
    private String description;
    private String command;
    private List<String> arguments;
    private int timeout;
}
