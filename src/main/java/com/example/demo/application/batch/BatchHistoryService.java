package com.example.demo.application.batch;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.batch.dto.BatchHistoryPageResponse;
import com.example.demo.application.batch.dto.BatchHistoryResponse;
import com.example.demo.application.batch.dto.BatchHistorySearchRequest;
import com.example.demo.application.batch.dto.BatchSearchParams;
import com.example.demo.application.batch.mapper.BatchMapper;
import com.example.demo.authentication.AuthenticationUtil;
import com.example.demo.domain.batch.BatchExecution;
import com.example.demo.domain.batch.repository.BatchExecutionRepository;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.util.PaginationHelper;
import com.example.demo.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * バッチ処理履歴の Application Service
 * ユーザーアクセス制御とビジネスロジックを担当
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class BatchHistoryService {

    @Autowired
    private BatchExecutionRepository batchExecutionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    /**
     * バッチ実行履歴を検索する
     * 
     * @param request 検索条件DTO
     * @return バッチ履歴ページレスポンス
     */
    public BatchHistoryPageResponse searchBatchHistory(BatchHistorySearchRequest request) {
        log.info("Search batch history: {}", request);

        // 現在のユーザーIDを取得
        Long currentUserId = authenticationUtil.getCurrentUserId();

        // 検索ユーザーIDを決定（アクセス制御）
        Long searchUserId = determineSearchUserId(currentUserId, request.getUserName());

        // ページネーション用のオフセットを計算
        int offset = PaginationHelper.calculateOffset(request.getPage(), request.getPageSize());

        // 検索パラメータオブジェクトを作成
        BatchSearchParams searchParams = BatchSearchParams.of(request, searchUserId, offset);

        // バッチ実行履歴を検索
        List<BatchExecution> executions = batchExecutionRepository.searchBatchExecution(searchParams);

        // 総件数を取得
        long totalCount = batchExecutionRepository.countBatchExecution(searchParams);

        // ユーザーIDをすべて抽出
        Set<Long> userIds = executions.stream().map(BatchExecution::getUserId).collect(Collectors.toSet());

        // 一度にすべてのユーザーを取得（バッチ取得でN+1問題を解決）
        final Map<Long, String> userNameMap = (!userIds.isEmpty() && authenticationUtil.hasAdminRole())
                ? userRepository.findByIds(userIds).stream()
                        .collect(Collectors.toMap(User::getId, User::getName,
                                (existing, replacement) -> existing))
                : Collections.emptyMap();

        // Entity を DTO に変換
        List<BatchHistoryResponse> items = executions.stream()
                .map(exec -> {
                    String displayUserName = authenticationUtil.hasAdminRole()
                            ? userNameMap.getOrDefault(exec.getUserId(), "Unknown")
                            : null;
                    return BatchMapper.toHistoryResponse(exec, displayUserName, exec.getUserId());
                })
                .collect(Collectors.toList());

        // 総ページ数を計算
        int totalPages = PaginationHelper.calculateTotalPages(totalCount, request.getPageSize());

        // レスポンスを構築
        return BatchHistoryPageResponse.builder()
                .content(items)
                .totalCount(totalCount)
                .totalPages(totalPages)
                .currentPage(request.getPage())
                .hasNextPage(PaginationHelper.hasNextPage(request.getPage(), totalPages))
                .hasPrevPage(PaginationHelper.hasPrevPage(request.getPage()))
                .build();
    }

    /**
     * 検索対象ユーザーIDを決定する
     * ユーザーのロールに基づいてアクセス制御を行う
     * 
     * @param currentUserId 現在のユーザーID
     * @param userName      検索対象ユーザー名（optional）
     * @return 検索対象ユーザーID（管理者が全員検索の場合は null）
     */
    private Long determineSearchUserId(Long currentUserId, String userName) {
        // 一般ユーザーの場合、自分のデータのみ
        if (!authenticationUtil.hasAdminRole()) {
            return currentUserId;
        }

        // 管理者の場合、ユーザー名が指定されていなければ全員検索
        if (StringUtil.isTrimmedNotEmpty(userName)) {
            return userRepository.findByName(userName)
                    .map(user -> user.getId())
                    .orElse(null);
        }

        // ユーザー名の指定なし → 全員検索
        return null;
    }

}
