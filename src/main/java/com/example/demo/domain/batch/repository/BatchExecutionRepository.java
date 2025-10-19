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

        /**
         * 検索条件でバッチ実行レコードを検索する
         * 
         * @param userId        ユーザーID（一般ユーザのフィルタリング用、管理者の場合はnull）
         * @param jobName       ジョブ名（部分一致）
         * @param status        ステータス
         * @param startDateFrom ジョブ開始日時の下限
         * @param endDateTo     ジョブ終了日時の上限
         * @param offset        オフセット
         * @param limit         件数制限
         * @return バッチ実行レコードのリスト
         */
        List<BatchExecution> searchBatchExecution(@Param("userId") Long userId, @Param("jobName") String jobName,
                        @Param("status") String status, @Param("startDateFrom") Object startDateFrom,
                        @Param("endDateTo") Object endDateTo, @Param("offset") int offset, @Param("limit") int limit);

        /**
         * 検索条件でバッチ実行レコードの総件数を取得する
         * 
         * @param userId        ユーザーID（一般ユーザのフィルタリング用、管理者の場合はnull）
         * @param jobName       ジョブ名（部分一致）
         * @param status        ステータス
         * @param startDateFrom ジョブ開始日時の下限
         * @param endDateTo     ジョブ終了日時の上限
         * @return 総件数
         */
        long countBatchExecution(@Param("userId") Long userId, @Param("jobName") String jobName,
                        @Param("status") String status, @Param("startDateFrom") Object startDateFrom,
                        @Param("endDateTo") Object endDateTo);
}
