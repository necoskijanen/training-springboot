package com.example.demo.domain.batch.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.batch.BatchExecution;

import java.util.List;

/**
 * バッチ実行履歴のリポジトリインターフェース
 */
@Mapper
public interface BatchExecutionRepository {

    /**
     * バッチ実行レコードを挿入する
     * 
     * @param execution 実行レコード
     */
    void insert(BatchExecution execution);

    /**
     * 実行IDでバッチ実行レコードを取得する
     * 
     * @param id 実行ID
     * @return バッチ実行レコード
     */
    BatchExecution findById(String id);

    /**
     * バッチ実行レコードを更新する
     * 
     * @param execution 実行レコード
     */
    void update(BatchExecution execution);

    /**
     * ユーザーの実行履歴をページング取得する
     * 
     * @param userId ユーザーID
     * @param offset オフセット
     * @param limit  件数制限
     * @return バッチ実行レコードのリスト
     */
    List<BatchExecution> findByUserIdWithPaging(@Param("userId") Long userId, @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * ユーザーの実行履歴の総件数を取得する
     * 
     * @param userId ユーザーID
     * @return 総件数
     */
    long countByUserId(Long userId);
}
