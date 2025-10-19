package com.example.demo.application.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.authentication.AuthenticationUtil;
import com.example.demo.config.BatchConfig;
import com.example.demo.domain.batch.BatchExecution;
import com.example.demo.domain.batch.ExecutionStatus;
import com.example.demo.domain.batch.repository.BatchExecutionRepository;

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

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @Autowired
    private BatchConfig batchConfig;

    // 実行中のバッチを追跡するマップ
    private final ConcurrentHashMap<String, CompletableFuture<BatchExecution>> executionMap = new ConcurrentHashMap<>();

    /**
     * バッチを開始する（ジョブID指定）
     * 
     * @param jobId ジョブID
     * @return 実行ID
     */
    public String startBatch(String jobId) {
        log.info("Starting batch execution for job: {}", jobId);

        // ジョブ設定を取得
        BatchConfig.Job job = getJobById(jobId);
        if (job == null) {
            throw new IllegalArgumentException("Job not found: " + jobId);
        }

        // 実行IDを生成
        String executionId = UUID.randomUUID().toString();

        // ユーザーIDを取得
        Long userId = authenticationUtil.getCurrentUserId();

        // データベースに実行レコードを作成（status=RUNNING）
        BatchExecution execution = BatchExecution.builder()
                .id(executionId)
                .jobId(jobId)
                .jobName(job.getName())
                .status(ExecutionStatus.RUNNING.name())
                .userId(userId)
                .startTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        batchExecutionRepository.insert(execution);
        log.info("Created execution record: {}", executionId);

        // 非同期実行を開始
        CompletableFuture<BatchExecution> future = CompletableFuture.supplyAsync(() -> {
            try {
                return executeBatch(executionId, job);
            } catch (Exception e) {
                log.error("Batch execution failed: {}", executionId, e);
                return handleExecutionError(executionId, e);
            }
        });

        // メモリマップに保存（高速ステータス確認用）
        executionMap.put(executionId, future);

        // 完了時にメモリマップから削除
        future.thenRun(() -> {
            executionMap.remove(executionId);
            log.info("Removed execution from memory map: {}", executionId);
        });

        log.info("Batch execution started asynchronously: {}", executionId);
        return executionId;
    }

    /**
     * ジョブIDでジョブ設定を取得する
     * 
     * @param jobId ジョブID
     * @return ジョブ設定
     */
    private BatchConfig.Job getJobById(String jobId) {
        if (batchConfig.getJobs() == null) {
            return null;
        }
        return batchConfig.getJobs().stream()
                .filter(job -> job.getId().equals(jobId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 実行ステータスを取得する
     * 
     * @param executionId 実行ID
     * @return 実行レコード
     */
    public BatchExecution getExecutionStatus(String executionId) {
        // メモリマップで実行中のプロセスを確認
        CompletableFuture<BatchExecution> future = executionMap.get(executionId);
        if (future != null && !future.isDone()) {
            // 実行中
            BatchExecution execution = batchExecutionRepository.findById(executionId);
            if (execution == null) {
                execution = new BatchExecution();
                execution.setId(executionId);
                execution.setStatus(ExecutionStatus.RUNNING.name());
            }
            return execution;
        }

        // メモリにない場合（または完了した場合）はDBから取得
        return batchExecutionRepository.findById(executionId);
    }

    /**
     * バッチを実行する
     * 
     * @param executionId 実行ID
     * @param job         ジョブ定義
     * @return 実行レコード
     * @throws Exception 実行エラー
     */
    private BatchExecution executeBatch(String executionId, BatchConfig.Job job) throws Exception {
        log.info("Execute batch: {} with command: {}", executionId, job.getCommand());

        // ProcessBuilder を作成
        List<String> command = commandBuilder.buildCommand(job);
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        // 作業ディレクトリを設定（デフォルト値は"./"）
        processBuilder.directory(new File(job.getWorkingDirectory()));

        // 環境変数を設定（デフォルト値は空マップ）
        processBuilder.environment().putAll(job.getEnvironment());

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
            updateExecutionStatus(executionId, ExecutionStatus.FAILED.name(), 124); // Timeout exit code
            return batchExecutionRepository.findById(executionId);
        }

        int exitCode = process.exitValue();
        long endTime = System.currentTimeMillis();
        log.info("Batch execution completed: {}, exitCode: {}, duration: {}ms", executionId, exitCode,
                endTime - startTime);

        // ステータスを更新
        String status = exitCode == 0
                ? ExecutionStatus.COMPLETED_SUCCESS.name()
                : ExecutionStatus.FAILED.name();
        updateExecutionStatus(executionId, status, exitCode);

        // 更新された実行レコードを返す
        return batchExecutionRepository.findById(executionId);
    }

    /**
     * 実行エラーを処理する
     * 
     * @param executionId 実行ID
     * @param e           例外
     * @return 実行レコード
     */
    private BatchExecution handleExecutionError(String executionId, Exception e) {
        updateExecutionStatus(executionId, ExecutionStatus.FAILED.name(), 1);
        return batchExecutionRepository.findById(executionId);
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
