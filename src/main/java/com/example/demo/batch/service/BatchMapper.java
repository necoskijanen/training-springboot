package com.example.demo.batch.service;

import org.mapstruct.Mapper;

import com.example.demo.batch.dto.BatchHistoryResponse;
import com.example.demo.batch.dto.HistoryItem;
import com.example.demo.batch.dto.JobResponse;
import com.example.demo.batch.dto.StatusResponse;
import com.example.demo.batch.entity.BatchExecution;
import com.example.demo.config.BatchConfig;

/**
 * バッチ処理の Entity と DTO 間の変換を担当する Mapper
 * MapStruct により実装が自動生成される
 */
@Mapper(componentModel = "spring")
public interface BatchMapper {

    /**
     * BatchConfig.Job を JobResponse に変換する
     * 
     * @param job
     * @return ジョブ情報
     */
    JobResponse toJobResponse(BatchConfig.Job job);

    /**
     * BatchExecution を StatusResponse に変換する
     * 
     * @param execution バッチ実行エンティティ
     * @return ステータスレスポンス
     */
    StatusResponse toStatusResponse(BatchExecution execution);

    /**
     * BatchExecution を HistoryItem に変換する
     * 
     * @param execution バッチ実行エンティティ
     * @return 履歴アイテム
     */
    HistoryItem toHistoryItem(BatchExecution execution);

    /**
     * BatchExecution を BatchHistoryResponse に変換する
     * 複数パラメータを持つカスタムマッピング
     * 
     * @param execution バッチ実行エンティティ
     * @param userName  ユーザー名（管理者用）
     * @param userId    ユーザーID
     * @return バッチ履歴レスポンス
     */
    BatchHistoryResponse toHistoryResponse(BatchExecution execution, String userName, Long userId);
}
