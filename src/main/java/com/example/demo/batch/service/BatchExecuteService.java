package com.example.demo.batch.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.batch.dto.ExecuteRequest;
import com.example.demo.batch.dto.ExecuteResponse;
import com.example.demo.batch.dto.ExecutionStatus;
import com.example.demo.batch.dto.JobResponse;
import com.example.demo.batch.dto.StatusResponse;
import com.example.demo.batch.entity.BatchExecution;
import com.example.demo.batch.exception.BatchDomainException;
import com.example.demo.batch.exception.BatchErrorCode;
import com.example.demo.batch.repository.BatchRepository;
import com.example.demo.config.BatchConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * バッチ処理サービス
 */
@Service
@Slf4j
@Transactional
public class BatchExecuteService {

    @Autowired
    private BatchRepository batchExecutionRepository;

    @Autowired
    private CommandBuilder commandBuilder;

    @Autowired
    private BatchConfig batchConfig;

    @Autowired
    private BatchMapper batchMapper;

    // 実行中のバッチを追跡するマップ
    private final ConcurrentHashMap<String, CompletableFuture<BatchExecution>> executionMap = new ConcurrentHashMap<>();

    /**
     * バッチを開始する（ジョブID・ユーザーID指定）
     * 非同期処理のため、ユーザーIDは事前に取得して渡す必要がある
     * 
     * @param jobId  ジョブID
     * @param userId ユーザーID（メインスレッドで取得済み）
     * @return 実行ID
     * @throws BatchDomainException ジョブが見つからない場合
     */
    public ExecuteResponse startBatch(ExecuteRequest request, Long userId) {
        log.info("Starting batch execution for job: {}, userId: {}", request, userId);

        // ジョブ設定を取得
        BatchConfig.Job job = getJobByIdOptional(request.getJobId())
                .orElseThrow(() -> new BatchDomainException(
                        BatchErrorCode.JOB_NOT_FOUND));

        BatchExecution execution = BatchExecution.startNew(job.getId(), job.getName(), userId);
        String executionId = execution.getId();

        // データベースに実行レコードを作成
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
        return new ExecuteResponse(executionId);
    }

    /**
     * 有効なジョブ一覧を取得する
     * 
     * @return 有効なジョブのリスト
     */
    public List<JobResponse> getAvailableJobs() {
        return batchConfig.getJobs().stream()
                .filter(BatchConfig.Job::isEnabled)
                .map(job -> batchMapper.toJobResponse(job))
                .collect(Collectors.toList());
    }

    /**
     * ジョブIDでジョブ設定を取得する（Optional版）
     * 
     * @param jobId ジョブID
     * @return ジョブ設定をラップしたOptional
     */
    private Optional<BatchConfig.Job> getJobByIdOptional(String jobId) {
        return batchConfig.getJobs().stream()
                .filter(job -> job.getId().equals(jobId))
                .findFirst();
    }

    /**
     * 実行ステータスを取得する（DTO版）
     * 
     * @param executionId 実行ID
     * @return ステータスレスポンス（Optional）
     */
    public Optional<StatusResponse> getExecutionStatus(String executionId) {
        return getExecutionEntity(executionId)
                .map(exec -> batchMapper.toStatusResponse(exec));
    }

    /**
     * 実行ステータスを取得する（Entity版）
     * 
     * @param executionId 実行ID
     * @return 実行レコード（Optional）
     */
    private Optional<BatchExecution> getExecutionEntity(String executionId) {
        // メモリマップで実行中のプロセスを確認
        CompletableFuture<BatchExecution> future = executionMap.get(executionId);
        if (future != null && !future.isDone()) {
            // 実行中の場合、メモリマップの情報を優先
            return batchExecutionRepository.findById(executionId)
                    .or(() -> Optional.of(createRunningExecution(executionId)));
        }

        // メモリにない場合（または完了した場合）はDBから取得
        return batchExecutionRepository.findById(executionId)
                .or(() -> {
                    log.warn("Batch execution not found: {}", executionId);
                    return Optional.empty();
                });
    }

    /**
     * 実行中のバッチ実行オブジェクトを作成する
     * 
     * @param executionId 実行ID
     * @return 実行中ステータスの BatchExecution
     */
    private BatchExecution createRunningExecution(String executionId) {
        BatchExecution execution = new BatchExecution();
        execution.setId(executionId);
        execution.setStatus(ExecutionStatus.RUNNING);
        return execution;
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
        boolean finished = process.waitFor(job.getTimeout(), TimeUnit.SECONDS);

        if (!finished) {
            log.warn("Batch execution timeout: {}", executionId);
            process.destroyForcibly();
            // ドメインメソッドでタイムアウトを処理
            updateExecution(executionId, execution -> execution.timeout());
            return batchExecutionRepository.findById(executionId).orElse(null);
        }

        int exitCode = process.exitValue();
        long endTime = System.currentTimeMillis();
        log.info("Batch execution completed: {}, exitCode: {}, duration: {}ms", executionId, exitCode,
                endTime - startTime);

        // ドメインメソッドでステータスを更新
        final int code = exitCode;
        updateExecution(executionId, execution -> {
            if (code == 0) {
                execution.completeSuccessfully();
            } else {
                execution.completeFailed(code);
            }
        });

        // 更新された実行レコードを返す
        return batchExecutionRepository.findById(executionId).orElse(null);
    }

    /**
     * 実行エラーを処理する
     * 
     * @param executionId 実行ID
     * @param e           例外
     * @return 実行レコード
     */
    private BatchExecution handleExecutionError(String executionId, Exception e) {
        updateExecution(executionId, execution -> execution.completeFailed(1));
        return batchExecutionRepository.findById(executionId).orElse(null);
    }

    /**
     * プロセスの標準出力と標準エラーを読み取る
     * 
     * @param process プロセス
     */
    private void readProcessOutput(Process process) {
        readStreamAsync(process.getInputStream(), "Process output");
        readStreamAsync(process.getErrorStream(), "Process error");
    }

    /**
     * プロセスのストリームを非同期で読み取る
     * 
     * @param stream    ストリーム
     * @param logPrefix ログプレフィックス
     */
    private void readStreamAsync(InputStream stream, String logPrefix) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("{}: {}", logPrefix, line);
                }
            } catch (Exception e) {
                log.warn("Error reading {}", logPrefix, e);
            }
        }).start();
    }

    /**
     * ドメインメソッドを使用して実行ステータスを更新する
     * 
     * @param executionId 実行ID
     * @param updater     実行オブジェクトを更新する関数型インターフェース
     */
    private void updateExecution(String executionId, Consumer<BatchExecution> updater) {
        batchExecutionRepository.findById(executionId).ifPresent(execution -> {
            try {
                updater.accept(execution);
                batchExecutionRepository.update(execution);
                log.info("Updated execution status: {}, status: {}, exitCode: {}", executionId, execution.getStatus(),
                        execution.getExitCode());
            } catch (BatchDomainException e) {
                log.warn("Domain validation failed: {}", e.getMessage());
            }
        });
    }
}
