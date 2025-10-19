package com.example.demo.application.batch;

import java.util.List;
import com.example.demo.config.BatchConfig;

/**
 * バッチコマンドビルダーインターフェース
 * 環境に応じてコマンド構築方法を切り替える
 */
public interface CommandBuilder {
    /**
     * ジョブ定義からコマンドをビルドする
     * 
     * @param job ジョブ定義
     * @return コマンドリスト
     */
    List<String> buildCommand(BatchConfig.Job job);
}
