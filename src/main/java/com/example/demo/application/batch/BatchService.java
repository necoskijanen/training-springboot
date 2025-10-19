package com.example.demo.application.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.BatchConfig;
import com.example.demo.domain.batch.repository.BatchExecutionRepository;
import com.example.demo.domain.batch.BatchExecution;

import lombok.extern.slf4j.Slf4j;

/**
 * バッチ処理サービス
 */
@Service
@Slf4j
@Transactional
public class BatchService {

    @Autowired
    private BatchExecutionRepository batchExecutionRepository;

    @Autowired
    private CommandBuilder commandBuilder;

    /**
     * バッチを非同期で実行する
     * 
     * @param executionId 実行ID
     * @param job         ジョブ定義
     */
    @Async
    public void executeBatchAsync(String executionId, BatchConfig.Job job) {
        log.info("Start async batch execution: {} for job: {}", executionId, job.getId());

        try {
            executeBatch(executionId, job);
        } catch (Exception e) {
            log.error("Batch execution failed: {}", executionId, e);
            updateExecutionStatus(executionId, "FAILED", 1);
        }
    }

    /**
     * バッチを実行する
     * 
     * @param executionId 実行ID
     * @param job         ジョブ定義
     * @throws Exception 実行エラー
     */
    private void executeBatch(String executionId, BatchConfig.Job job) throws Exception {
        log.info("Execute batch: {} with command: {}", executionId, job.getCommand());

        // ProcessBuilder を作成
        List<String> command = commandBuilder.buildCommand(job);
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        // 作業ディレクトリを設定
        if (job.getWorkingDirectory() != null && !job.getWorkingDirectory().isEmpty()) {
            processBuilder.directory(new File(job.getWorkingDirectory()));
        }

        // 環境変数を設定
        if (job.getEnvironment() != null && !job.getEnvironment().isEmpty()) {
            processBuilder.environment().putAll(job.getEnvironment());
        }

        // プロセスを開始
        long startTime = System.currentTimeMillis();
        Process process = processBuilder.start();

        // 標準出力と標準エラーを読み取る
        readProcessOutput(process);

        // プロセスの終了を待機（タイムアウト設定）
        boolean finished = process.waitFor(job.getTimeout(), java.util.concurrent.TimeUnit.SECONDS);

        if (!finished) {
            log.warn("Batch execution timeout: {}", executionId);
            process.destroyForcibly();
            updateExecutionStatus(executionId, "FAILED", 124); // Timeout exit code
            return;
        }

        int exitCode = process.exitValue();
        long endTime = System.currentTimeMillis();
        log.info("Batch execution completed: {}, exitCode: {}, duration: {}ms", executionId, exitCode,
                endTime - startTime);

        // ステータスを更新
        if (exitCode == 0) {
            updateExecutionStatus(executionId, "COMPLETED_SUCCESS", null);
        } else {
            updateExecutionStatus(executionId, "FAILED", exitCode);
        }
    }

    /**
     * プロセスの標準出力と標準エラーを読み取る
     * 
     * @param process プロセス
     */
    private void readProcessOutput(Process process) {
        // 標準出力を読み取るスレッド
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("Process output: {}", line);
                }
            } catch (Exception e) {
                log.warn("Error reading process output", e);
            }
        }).start();

        // 標準エラーを読み取るスレッド
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("Process error: {}", line);
                }
            } catch (Exception e) {
                log.warn("Error reading process error", e);
            }
        }).start();
    }

    /**
     * 実行ステータスを更新する
     * 
     * @param executionId 実行ID
     * @param status      ステータス
     * @param exitCode    終了コード
     */
    private void updateExecutionStatus(String executionId, String status, Integer exitCode) {
        BatchExecution execution = batchExecutionRepository.findById(executionId);
        if (execution != null) {
            execution.setStatus(status);
            execution.setExitCode(exitCode);
            execution.setEndTime(LocalDateTime.now());
            batchExecutionRepository.update(execution);
            log.info("Updated execution status: {}, status: {}, exitCode: {}", executionId, status, exitCode);
        }
    }
}
