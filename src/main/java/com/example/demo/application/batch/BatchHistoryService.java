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
import com.example.demo.application.batch.dto.HistoryItem;
import com.example.demo.application.batch.dto.HistoryResponse;
import com.example.demo.application.batch.mapper.BatchMapper;
import com.example.demo.domain.batch.BatchExecution;
import com.example.demo.domain.batch.repository.BatchRepository;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.exception.UserDomainException;
import com.example.demo.domain.user.exception.UserErrorCode;
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
        private BatchRepository batchRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private BatchMapper batchMapper;

        /**
         * ユーザーのバッチ実行履歴を取得する（ページネーション対応）
         * 
         * @param userId   ユーザーID
         * @param page     ページ番号（0から開始）
         * @param pageSize ページサイズ
         * @return 履歴レスポンス
         */
        public HistoryResponse getHistory(Long userId, int page, int pageSize) {
                log.info("Get batch history for user: {}, page: {}, pageSize: {}", userId, page, pageSize);

                // 履歴を取得
                BatchSearchCriteria params = BatchSearchCriteria.of(userId, page, pageSize);
                List<BatchExecution> executions = batchRepository.searchBatchExecution(params);

                // 総件数を取得
                long totalCount = batchRepository.countByUserId(userId);

                // レスポンスを構築
                List<HistoryItem> items = executions.stream()
                                .map(exec -> batchMapper.toHistoryItem(exec))
                                .collect(Collectors.toList());

                int totalPages = PaginationHelper.calculateTotalPages(totalCount, pageSize);
                return HistoryResponse.builder()
                                .items(items)
                                .page(page)
                                .size(pageSize)
                                .totalCount(totalCount)
                                .totalPages(totalPages)
                                .build();
        }

        /**
         * バッチ実行履歴を検索する
         * 
         * @param request 検索条件DTO
         * @return バッチ履歴ページレスポンス
         */
        public BatchHistoryPageResponse searchBatchHistory(BatchHistorySearchRequest request, Long userId) {
                log.info("Search batch history: {}", request);

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserDomainException(UserErrorCode.USER_NOT_FOUND));

                // 検索ユーザーIDを決定（アクセス制御）
                Long searchUserId = determineSearchUserId(user, request.getUserName());

                // 検索パラメータオブジェクトを作成
                BatchSearchCriteria searchParams = BatchSearchCriteria.of(request, searchUserId);

                // バッチ実行履歴を検索
                List<BatchExecution> executions = batchRepository.searchBatchExecution(searchParams);

                // 総件数を取得
                long totalCount = batchRepository.countBatchExecution(searchParams);

                // ユーザーIDをすべて抽出
                Set<Long> userIds = executions.stream().map(BatchExecution::getUserId).collect(Collectors.toSet());

                // 一度にすべてのユーザーを取得（バッチ取得でN+1問題を解決）
                final Map<Long, String> userNameMap = (!userIds.isEmpty() && user.hasAdminRole())
                                ? userRepository.findByIds(userIds).stream()
                                                .collect(Collectors.toMap(User::getId, User::getName,
                                                                (existing, replacement) -> existing))
                                : Collections.emptyMap();

                // Entity を DTO に変換
                List<BatchHistoryResponse> items = executions.stream()
                                .map(exec -> {
                                        String userName = user.hasAdminRole()
                                                        ? userNameMap.getOrDefault(exec.getUserId(), "Unknown")
                                                        : null;
                                        return batchMapper.toHistoryResponse(exec, userName, exec.getUserId());
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
         * @param user     検索実行ユーザー
         * @param userName 検索対象ユーザー名（optional）
         * @return 検索対象ユーザーID（管理者が全員検索の場合は null）
         */
        private Long determineSearchUserId(User user, String userName) {
                // 一般ユーザーの場合、自分のデータのみ
                if (!user.hasAdminRole()) {
                        return user.getId();
                }

                // 管理者の場合、ユーザー名が指定されていなければ全員検索
                if (StringUtil.isTrimmedNotEmpty(userName)) {
                        return userRepository.findByName(userName)
                                        .map(u -> u.getId())
                                        .orElse(null);
                }

                // ユーザー名の指定なし → 全員検索
                return null;
        }

}
