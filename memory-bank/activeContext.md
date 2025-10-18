# Active Context

## Current Work Focus

### Batch Processing Infrastructure Expansion
- Batch execution infrastructure has been partially created (BatchController, BatchService, BatchConfig)
- Batch configuration YAML file in place with test job definitions
- Need to expand service implementation and connect to REST API
- Status: In Progress - Expanding batch service implementation and documenting current state

## Recent Changes

### 2025/10/19 - Batch Infrastructure Implementation & Memory Bank Update
- **BatchController.java**: Routes for batch UI pages at `/admin/batch/start`, `/user/batch/start`, `/admin/batch/history`, `/user/batch/history`
- **BatchService.java**: Service class created with @Service, @Slf4j, and @Transactional annotations
- **BatchConfig.java**: Configuration properties mapping batch job definitions from YAML
- **batch/config.yml**: Job definitions with wait_time_test and wait_time_error test jobs
- **UI Templates**: batch/start.html and batch/history.html templates created
- **Test Scripts**: wait_time.sh and wait_time.bat for batch execution testing
- **Memory Bank Update**: Updating activeContext.md and progress.md to reflect current implementation state

### 2025/10/17 - Memory Bank Creation
- **Created**: `projectbrief.md` - Project scope, goals, requirements
- **Created**: `productContext.md` - Problem domain and user journeys
- **Created**: `systemPatterns.md` - Architecture and design patterns
- **Created**: `techContext.md` - Technology stack and development environment

## Current System State

### Implemented Features
1. **Login/Logout System**
   - Spring Security configured
   - BCrypt password encoding
   - Custom authentication success handler for role-based redirects

2. **Role-Based Access Control**
   - Two roles: ADMIN and USER
   - URL-based authorization rules
   - Separate home pages for each role

3. **Database Schema**
   - User management (user_master table)
   - Role definitions (role_definition table)
   - User-role relationships (user_role table)
   - Support for many-to-many user-role associations

4. **User Interface**
   - Login page (Thymeleaf template)
   - Admin home page
   - User home page

### Partially Implemented
1. **Logging System**
   - @Slf4j annotation added to BatchController
   - logback-spring.xml exists but needs JSON configuration
   - Need comprehensive logging throughout application layers

2. **Batch Execution Interface**
   - ✅ BatchController created with UI routing
   - ✅ BatchConfig created for configuration management
   - ✅ Templates created (batch/start.html, batch/history.html)
   - ❌ Batch execution logic not yet implemented in service
   - ❌ Process management (ProcessBuilder) not yet integrated

3. **Batch Status Monitoring**
   - ❌ No REST API endpoints (/api/batch/execute, /api/batch/status/{id})
   - ❌ No asynchronous execution tracking
   - ❌ No CompletableFuture-based status tracking

4. **Batch Execution History**
   - ❌ No batch_execution_history table in schema
   - ✅ Templates exist but not connected to data
   - ❌ No pagination implemented


## Next Steps

### Immediate Priorities
1. **Implement Batch Service Logic**
   - Add batch execution methods to BatchService
   - Implement ProcessBuilder-based execution
   - Create asynchronous execution with CompletableFuture
   - Add execution status tracking

2. **Create Batch Domain Models**
   - Create BatchExecution entity
   - Create ExecutionStatus enum
   - Add necessary repositories for batch data

3. **Database Schema Update for Batch History**
   - Add batch_execution_history table to schema.sql
   - Define fields: id, job_id, start_time, end_time, status, exit_code, user_id
   - Create indexes for performance

4. **Implement REST API for Batch Operations**
   - Create @RestController for /api/batch endpoints
   - Implement /api/batch/execute POST endpoint
   - Implement /api/batch/status/{id} GET endpoint
   - Support 5-second polling from frontend

5. **Complete Batch UI Integration**
   - Implement job selection in batch/start.html
   - Add execution trigger button
   - Implement real-time status display with polling
   - Connect batch/history.html to data

6. **Enhance Logging**
   - Configure Logback for JSON output
   - Add comprehensive logging to all layers
   - Implement security event logging
   - Add request/response tracking

7. **Implement Pagination**
   - Add MyBatis pagination queries
   - Create Pageable DTOs
   - Implement frontend pagination UI

### Technical Decisions Resolved
- **Batch Configuration**: YAML-based (batch/config.yml) with flexible job definitions
- **Test Environment**: Shell and batch scripts for testing (wait_time.sh/.bat)
- **Async Processing**: Planned CompletableFuture approach
- **Role-Based Access**: Both ADMIN and USER roles can access batch features
- **Timeout Configuration**: 60 seconds per job (configured in config.yml)

### Batch Start Screen Specification (確定: 2025/10/19)

**UI レイアウト：**
- 上部：ジョブ選択ドロップダウン（enabled:trueのみ）+ 詳細ボタン
- 中部：パラメータ表示（読み取り専用テーブル） + 実行ボタン
- 下部：実行履歴テーブル（ページネーション対応）

**REST API（4エンドポイント）：**
1. GET /api/batch/jobs → ジョブリスト
2. POST /api/batch/execute (jobId送信) → executionId返却
3. GET /api/batch/status/{executionId} → ステータスポーリング用（5秒ごと）
4. GET /api/batch/history?page=0&size=10 → 履歴取得

**フロントエンド：**
- Vanilla JavaScript（fetch + setInterval + DOM操作）
- 複雑性なし - Reactなし

**重要な仕様：**
- executionId: サーバーで UUID.randomUUID() で生成
- パラメータ: 固定値、ユーザーは変更不可
- ステータス: RUNNING, COMPLETED_SUCCESS, FAILED
- ポーリング間隔: 5秒
- 履歴テーブル: batch_execution_history に保存

**次の実装タスク：**
1. schema.sql に batch_execution_history テーブル追加
2. ExecutionStatus enum + BatchExecution エンティティ作成
3. BatchRestController で 4つの API 実装
4. BatchService で ProcessBuilder 実行 + UUID 管理
5. batch/start.html を完全実装
6. JavaScript で API 連携 + ポーリング実装



## Development Guidelines Reminder

### Code Standards
- All code comments in English
- Follow DDD, TDD, FP principles
- Separation of concerns, high cohesion, loose coupling
- Domain logic independent of frameworks

### Security Practices
- Validate all user input
- Never log personal information
- Escape data before sending to other systems
- Apply defense in depth

### Git Workflow
- Commit messages in English: `<type>(<scope>): <subject>`
- Create feature branches: `feature/<issue>-<description>`
- One logical change per commit
- Use pull requests for code review

### Testing Approach
- Write tests first (TDD)
- Test as specifications
- Small increments
- Continuous refactoring

## Context for Next Session

When resuming work on this project:

1. **Review Memory Bank**: Read all core memory bank files to understand current state
2. **Check Open Questions**: Address any unresolved technical decisions
3. **Follow Next Steps**: Implement features in priority order
4. **Update Documentation**: Keep memory bank files current with changes
5. **Maintain Standards**: Adhere to coding and security guidelines

### Key Files to Review
- `docs/specifications.md` - Original requirements
- `docs/rules/*.md` - Coding standards and practices
- `memory-bank/*.md` - Current project state and context

### Current Technology Context
- Java 21 with Spring Boot 3.5.6
- MyBatis for data access
- H2 in-memory database
- Spring Security for authentication/authorization
- Thymeleaf for server-side rendering
