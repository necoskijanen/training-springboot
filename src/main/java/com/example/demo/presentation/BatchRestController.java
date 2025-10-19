package com.example.demo.presentation;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.batch.BatchService;
import com.example.demo.authentication.AuthenticationUtil;
import com.example.demo.config.BatchConfig;
import com.example.demo.domain.batch.BatchExecution;
import com.example.demo.domain.batch.repository.BatchExecutionRepository;
import com.example.demo.domain.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * バッチ処理の REST API コントローラー
 */
@RestController
@RequestMapping("/api/batch")
@Slf4j
public class BatchRestController {

    @Autowired
    private BatchConfig batchConfig;

    @Autowired
    private BatchService batchService;

    @Autowired
    private BatchExecutionRepository batchExecutionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 有効なバッチジョブ一覧を取得する
     * 
     * @return ジョブ情報のリスト
     */
    @GetMapping("/jobs")
    public ResponseEntity<List<BatchConfig.Job>> getAvailableJobs() {
        List<BatchConfig.Job> enabledJobs = batchConfig.getJobs()
                .stream()
                .filter(BatchConfig.Job::isEnabled)
                .toList();

        log.info("Available jobs: {}", enabledJobs.size());
        return ResponseEntity.ok(enabledJobs);
    }

    /**
     * バッチを実行する
     * 
     * @param jobId          ジョブID
     * @param authentication 認証情報
     * @return 実行ID
     */
    @PostMapping("/execute")
    public ResponseEntity<Map<String, String>> executeBatch(@RequestParam String jobId,
            Authentication authentication) {
        log.info("Execute batch job: {}", jobId);

        // ジョブを検索
        BatchConfig.Job job = batchConfig.getJobs()
                .stream()
                .filter(j -> j.getId().equals(jobId) && j.isEnabled())
                .findFirst()
                .orElse(null);

        if (job == null) {
            log.warn("Job not found or disabled: {}", jobId);
            return ResponseEntity.badRequest().build();
        }

        // 実行IDを生成
        String executionId = UUID.randomUUID().toString();
        String username = AuthenticationUtil.getCurrentUsername(authentication);
        Long userId = userRepository.findByName(username)
                .map(u -> u.getId())
                .orElse(null);

        if (userId == null) {
            log.warn("User not found: {}", username);
            return ResponseEntity.badRequest().build();
        }

        // バッチ実行レコードを作成
        BatchExecution execution = BatchExecution.builder()
                .id(executionId)
                .jobId(jobId)
                .jobName(job.getName())
                .status("RUNNING")
                .userId(userId)
                .startTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        batchExecutionRepository.insert(execution);
        log.info("Batch execution created: {} for job: {}", executionId, jobId);

        // バッチを非同期で実行
        batchService.executeBatchAsync(executionId, job);

        Map<String, String> response = new HashMap<>();
        response.put("executionId", executionId);
        return ResponseEntity.ok(response);
    }

    /**
     * バッチ実行のステータスを取得する
     * 
     * @param executionId 実行ID
     * @return ステータス情報
     */
    @GetMapping("/status/{executionId}")
    public ResponseEntity<Map<String, Object>> getExecutionStatus(@PathVariable String executionId) {
        log.debug("Get execution status: {}", executionId);

        BatchExecution execution = batchExecutionRepository.findById(executionId);
        if (execution == null) {
            log.warn("Execution not found: {}", executionId);
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", execution.getId());
        response.put("jobId", execution.getJobId());
        response.put("jobName", execution.getJobName());
        response.put("status", execution.getStatus());
        response.put("exitCode", execution.getExitCode());
        response.put("startTime", execution.getStartTime());
        response.put("endTime", execution.getEndTime());

        return ResponseEntity.ok(response);
    }

    /**
     * バッチ実行履歴をページング取得する
     * 
     * @param page           ページ番号（0始まり）
     * @param size           1ページの件数
     * @param authentication 認証情報
     * @return 実行履歴のページング結果
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getBatchHistory(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, Authentication authentication) {
        log.info("Get batch history: page={}, size={}", page, size);

        String username = AuthenticationUtil.getCurrentUsername(authentication);
        Long userId = userRepository.findByName(username)
                .map(u -> u.getId())
                .orElse(null);

        if (userId == null) {
            log.warn("User not found: {}", username);
            return ResponseEntity.badRequest().build();
        }

        int offset = page * size;

        List<BatchExecution> executions = batchExecutionRepository.findByUserIdWithPaging(userId, offset, size);
        long totalCount = batchExecutionRepository.countByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("content", executions);
        response.put("totalElements", totalCount);
        response.put("totalPages", (totalCount + size - 1) / size);
        response.put("currentPage", page);
        response.put("pageSize", size);

        return ResponseEntity.ok(response);
    }
}
