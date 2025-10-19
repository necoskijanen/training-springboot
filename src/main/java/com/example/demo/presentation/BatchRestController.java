package com.example.demo.presentation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.batch.BatchService;
import com.example.demo.application.batch.dto.BatchHistoryPageResponse;
import com.example.demo.application.batch.dto.BatchHistoryResponse;
import com.example.demo.authentication.AuthenticationUtil;
import com.example.demo.config.BatchConfig;
import com.example.demo.domain.batch.BatchExecution;
import com.example.demo.domain.batch.repository.BatchExecutionRepository;
import com.example.demo.domain.user.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * バッチ処理のREST APIコントローラ
 */
@RestController
@RequestMapping("/api/batch")
@Slf4j
@PreAuthorize("isAuthenticated()")
public class BatchRestController {

    @Autowired
    private BatchService batchService;

    @Autowired
    private BatchExecutionRepository batchExecutionRepository;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @Autowired
    private BatchConfig batchConfig;

    @Autowired
    private UserRepository userRepository;

    /**
     * 有効なジョブ一覧を取得する
     * 
     * @return ジョブのDTO リスト
     */
    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> getJobs() {
        log.info("Get jobs list");

        List<JobResponse> jobs = batchConfig.getJobs().stream()
                .filter(BatchConfig.Job::isEnabled)
                .map(job -> JobResponse.builder()
                        .id(job.getId())
                        .name(job.getName())
                        .description(job.getDescription())
                        .command(job.getCommand())
                        .arguments(job.getArguments())
                        .timeout(job.getTimeout())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }

    /**
     * バッチを実行する
     * 
     * @param request 実行リクエスト（jobId を含む）
     * @return 実行ID
     */
    @PostMapping("/execute")
    public ResponseEntity<ExecuteResponse> executeBatch(@RequestBody ExecuteRequest request) {
        log.info("Execute batch for job: {}", request.getJobId());

        try {
            String executionId = batchService.startBatch(request.getJobId());
            return ResponseEntity.ok(new ExecuteResponse(executionId));
        } catch (IllegalArgumentException e) {
            log.warn("Job not found: {}", request.getJobId());
            return ResponseEntity.badRequest()
                    .body(new ExecuteResponse(null, "Job not found: " + request.getJobId()));
        } catch (Exception e) {
            log.error("Failed to start batch execution", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ExecuteResponse(null, "Failed to start batch execution"));
        }
    }

    /**
     * バッチ実行のステータスを取得する
     * 
     * @param executionId 実行ID
     * @return ステータスレスポンス
     */
    @GetMapping("/status/{executionId}")
    public ResponseEntity<StatusResponse> getStatus(@PathVariable String executionId) {
        log.debug("Get status for execution: {}", executionId);

        BatchExecution execution = batchService.getExecutionStatus(executionId);

        if (execution == null) {
            return ResponseEntity.notFound().build();
        }

        StatusResponse response = StatusResponse.builder()
                .status(execution.getStatus())
                .exitCode(execution.getExitCode())
                .startTime(execution.getStartTime())
                .endTime(execution.getEndTime())
                .jobName(execution.getJobName())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * バッチ実行履歴を取得する（ページネーション対応）
     * 
     * @param page ページ番号（0 から開始）
     * @param size ページサイズ
     * @return 履歴のページネーション結果
     */
    @GetMapping("/history")
    public ResponseEntity<HistoryResponse> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get batch history: page={}, size={}", page, size);

        Long userId = authenticationUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // ページネーション用のオフセット・リミットを計算
        int offset = page * size;
        int limit = size;

        // 履歴を取得
        List<BatchExecution> executions = batchExecutionRepository.findByUserIdWithPaging(userId, offset, limit);

        // 総件数を取得
        long totalCount = batchExecutionRepository.countByUserId(userId);

        // レスポンスを構築
        List<HistoryItem> items = executions.stream()
                .map(exec -> HistoryItem.builder()
                        .id(exec.getId())
                        .jobId(exec.getJobId())
                        .jobName(exec.getJobName())
                        .status(exec.getStatus())
                        .startTime(exec.getStartTime())
                        .endTime(exec.getEndTime())
                        .exitCode(exec.getExitCode())
                        .build())
                .collect(Collectors.toList());

        HistoryResponse response = HistoryResponse.builder()
                .items(items)
                .page(page)
                .size(size)
                .totalCount(totalCount)
                .totalPages((totalCount + size - 1) / size)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * バッチ実行履歴を検索する（高度な検索条件対応）
     * 
     * @param jobName       バッチ名（部分一致）
     * @param status        ステータス
     * @param startDateFrom ジョブ開始日時の下限（datetime-local形式）
     * @param endDateTo     ジョブ終了日時の上限（datetime-local形式）
     * @param userId        実行ユーザーID（管理者用）
     * @param page          ページ番号（0 から開始）
     * @param size          ページサイズ（デフォルト10）
     * @return 検索結果のページネーション
     */
    @GetMapping("/history/search")
    public ResponseEntity<BatchHistoryPageResponse> searchBatchHistory(
            @RequestParam(required = false) String jobName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDateTime startDateFrom,
            @RequestParam(required = false) LocalDateTime endDateTo,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Search batch history: jobName={}, status={}, startDateFrom={}, endDateTo={}, page={}, size={}",
                jobName, status, startDateFrom, endDateTo, page, size);

        Long currentUserId = authenticationUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 一般ユーザーの場合、userIdは自分に固定
        Long searchUserId = currentUserId;
        if (authenticationUtil.hasAdminRole() && userId != null) {
            searchUserId = userId;
        }

        // ページネーション用のオフセット・リミットを計算
        int offset = page * size;
        int limit = size;

        // 検索を実行
        List<BatchExecution> executions = batchExecutionRepository.searchBatchExecution(
                searchUserId, jobName, status, startDateFrom, endDateTo, offset, limit);

        // 総件数を取得
        long totalCount = batchExecutionRepository.countBatchExecution(
                searchUserId, jobName, status, startDateFrom, endDateTo);

        // レスポンスを構築
        List<BatchHistoryResponse> items = executions.stream()
                .map(exec -> {
                    // 一般ユーザーの場合、ユーザー名は不要（自分のデータのみ）
                    // 管理者の場合、ユーザー名を取得
                    String userName = null;
                    if (authenticationUtil.hasAdminRole()) {
                        userName = userRepository.findById(exec.getUserId())
                                .map(user -> user.getName())
                                .orElse("Unknown");
                    }

                    return BatchHistoryResponse.builder()
                            .executionId(exec.getId())
                            .jobName(exec.getJobName())
                            .status(exec.getStatus())
                            .exitCode(exec.getExitCode())
                            .userId(exec.getUserId())
                            .userName(userName)
                            .startTime(exec.getStartTime())
                            .endTime(exec.getEndTime())
                            .build();
                })
                .collect(Collectors.toList());

        int totalPages = (int) ((totalCount + size - 1) / size);
        BatchHistoryPageResponse response = BatchHistoryPageResponse.builder()
                .content(items)
                .totalCount(totalCount)
                .totalPages(totalPages)
                .currentPage(page)
                .hasNextPage(page < totalPages - 1)
                .hasPrevPage(page > 0)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * ジョブのDTOレスポンス
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JobResponse {
        private String id;
        private String name;
        private String description;
        private String command;
        private List<String> arguments;
        private int timeout;
    }

    /**
     * 実行リクエスト
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecuteRequest {
        private String jobId;
    }

    /**
     * 実行レスポンス
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExecuteResponse {
        private String executionId;
        private String error;

        public ExecuteResponse(String executionId) {
            this.executionId = executionId;
            this.error = null;
        }
    }

    /**
     * ステータスレスポンス
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatusResponse {
        private String status;
        private Integer exitCode;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String jobName;
    }

    /**
     * 履歴アイテム
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoryItem {
        private String id;
        private String jobId;
        private String jobName;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer exitCode;
    }

    /**
     * 履歴レスポンス（ページネーション対応）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoryResponse {
        private List<HistoryItem> items;
        private int page;
        private int size;
        private long totalCount;
        private long totalPages;
    }
}
