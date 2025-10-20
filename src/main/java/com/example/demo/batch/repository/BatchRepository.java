package com.example.demo.batch.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.batch.entity.BatchExecution;
import com.example.demo.batch.service.BatchSearchCriteria;

import java.util.List;
import java.util.Optional;

/**
 * バッチ実行履歴のリポジトリインターフェース
 */
@Mapper
public interface BatchRepository {

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
         * @return バッチ実行レコード（取得できない場合は空のOptional）
         */
        Optional<BatchExecution> findById(String id);

        /**
         * バッチ実行レコードを更新する
         * 
         * @param execution 実行レコード
         */
        void update(BatchExecution execution);

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
         * @param params 検索パラメータオブジェクト
         * @return バッチ実行レコードのリスト
         */
        List<BatchExecution> searchBatchExecution(BatchSearchCriteria params);

        /**
         * 検索条件でバッチ実行レコードの総件数を取得する
         * 
         * @param params 検索パラメータオブジェクト
         * @return 総件数
         */
        long countBatchExecution(BatchSearchCriteria params);

}
