package com.example.demo.presentation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.batch.BatchExecuteService;
import com.example.demo.application.batch.BatchHistoryService;
import com.example.demo.application.batch.dto.BatchHistoryPageResponse;
import com.example.demo.application.batch.dto.BatchHistorySearchRequest;
import com.example.demo.application.batch.dto.ExecuteRequest;
import com.example.demo.application.batch.dto.ExecuteResponse;
import com.example.demo.application.batch.dto.HistoryResponse;
import com.example.demo.application.batch.dto.JobResponse;
import com.example.demo.application.batch.dto.StatusResponse;
import com.example.demo.authentication.AuthenticationUtil;

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
    private BatchExecuteService batchService;

    @Autowired
    private BatchHistoryService batchHistoryService;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    /**
     * 有効なジョブ一覧を取得する
     * 
     * @return ジョブのDTO リスト
     */
    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> getJobs() {
        log.info("Get jobs list");
        return ResponseEntity.ok(batchService.getAvailableJobs());
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
            // メインスレッドで事前にユーザーIDを取得
            Long userId = authenticationUtil.getCurrentUserId();
            ExecuteResponse response = batchService.startBatch(request, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Job not found: {}", request);
            return ResponseEntity.badRequest()
                    .body(new ExecuteResponse(null, "Job not found: " + request));
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

        return batchService.getExecutionStatus(executionId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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

        return authenticationUtil.getCurrentUserIdOptional()
                .map(userId -> ResponseEntity.ok(batchHistoryService.getHistory(userId, page, size)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * バッチ実行履歴を検索する（高度な検索条件対応）
     * 
     * @param request 検索条件DTO（クエリパラメータにマッピング）
     * @return 検索結果のページネーション
     */
    @GetMapping("/history/search")
    public ResponseEntity<BatchHistoryPageResponse> searchBatchHistory(
            @ModelAttribute BatchHistorySearchRequest request) {
        log.info("Search batch history: {}", request);

        // Application Service にビジネスロジックを委譲
        Long userId = authenticationUtil.getCurrentUserId();
        BatchHistoryPageResponse response = batchHistoryService.searchBatchHistory(request, userId);

        return ResponseEntity.ok(response);
    }

}
