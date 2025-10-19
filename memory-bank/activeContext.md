# Active Context

## Current Work Focus

### Batch History Search Implementation - IN PROGRESS
- Backend API for advanced batch history search with filtering and pagination
- Support for role-based access control (admin can search all users, users can search own history)
- Search conditions: job name, status, date ranges (start/end)
- Pagination: 10 items per page with page navigation (previous/next/page number)
- Status: 🔄 BACKEND COMPLETED, FRONTEND PENDING

## Recent Changes

### 2025/10/19 - Batch History Search Backend Implementation (午後中期)

#### DTOs Created with Lombok
- **BatchHistorySearchRequest** - Search conditions (jobName, status, date ranges, userId, page, pageSize)
- **BatchHistoryResponse** - Single history item response with user name for admin
- **BatchHistoryPageResponse** - Paginated response with page metadata

#### MyBatis Mapper Enhancement
- **searchBatchExecution()** - Dynamic SQL query with date range filtering (CDATA sections for comparison operators)
- **countBatchExecution()** - Count query for total records matching search criteria
- Uses `<where>` and `<if>` tags for optional filters
- Results ordered by end_time DESC
- LIMIT/OFFSET pagination implemented

#### BatchExecutionRepository Interface
- Added searchBatchExecution() method signature
- Added countBatchExecution() method signature
- Full parameter documentation with JavaDoc

#### BatchRestController Enhancement
- **GET /api/batch/history/search** - New advanced search endpoint
- Query parameters: jobName, status, startDateFrom, startDateTo, endDateFrom, endDateTo, userId, page, size
- Role-based filtering:
  - General users: Can only search their own history (userId fixed)
  - Admins: Can search by specific userId if provided
- Response includes user names for admin view (null for regular users)
- Page metadata: totalCount, totalPages, currentPage, hasNextPage, hasPrevPage
- Proper HTTP status codes and error handling

#### XML MapperXML Fixes
- Used CDATA sections `<![CDATA[...]]>` to properly handle comparison operators (`>=`, `<=`)
- Prevents XML parsing errors with comparison operators in WHERE clauses

### Batch Processing Implementation - COMPLETED
- Batch processing infrastructure fully implemented with CompletableFuture and UUID-based execution tracking
- REST API endpoints created (4 endpoints for job list, execution, status, and history)
- Frontend JavaScript implementation complete with 5-second polling
- Database integration with batch_execution_history table
- Status: ✅ COMPLETED - Ready for testing and deployment

### 2025/10/19 - Complete Batch Processing Implementation (午後後期)

#### BatchService.java - CompletableFuture & Memory Map Integration
- **startBatch(jobId)** - Main entry point: UUID generation → DB insert → Async execution
- **executeBatch()** - ProcessBuilder execution with timeout and exit code handling
- **getExecutionStatus()** - Dual-layer status check: Memory map (fast) → DB fallback
- **CompletableFuture Integration**:
  - `ConcurrentHashMap<String, CompletableFuture<BatchExecution>>` for tracking
  - Auto-cleanup on completion
  - Enables fast polling without DB queries for running tasks
- **AuthenticationUtil.getCurrentUserId()** - Get current user ID from authentication context
- **Database Integration**: Status updates persisted to `batch_execution_history` table

#### BatchRestController.java - Complete REST API (4 endpoints)
- **GET /api/batch/jobs** - List enabled jobs (JobResponse DTOs)
- **POST /api/batch/execute** - Start batch execution (returns executionId)
- **GET /api/batch/status/{executionId}** - Get execution status with full metadata
- **GET /api/batch/history?page=0&size=10** - Paginated history (HistoryResponse with pagination)
- **Error Handling**: Proper HTTP status codes and error messages
- **Security**: `@PreAuthorize("isAuthenticated()")` on all endpoints
- **User Isolation**: History filtered by current user ID

#### batch/start.html JavaScript Implementation
- **loadAvailableJobs()** - Fetch and populate job dropdown
- **onJobChanged()** - Update parameters table when job selected
- **executeBatch()** - POST to /api/batch/execute with CSRF token
- **startPolling()** - 5-second interval polling via setInterval
- **checkStatus()** - Poll /api/batch/status/{id}, stop on completion
- **loadHistory()** - Paginated history loading with proper response mapping
- **Pagination Controls** - Previous/Next buttons with proper state management
- **Status Display** - Color-coded badges (RUNNING/SUCCESS/FAILED)
- **Error Handling** - User-friendly alert messages

#### AuthenticationUtil.java Enhancement
- **getCurrentUserId()** - New method: Extract user ID from authentication username
- Dependency: Injected UserRepository to look up user by name
- Returns Long user ID or null if not authenticated

#### Data Model & Schema
- **ExecutionStatus enum** - RUNNING, COMPLETED_SUCCESS, FAILED
- **BatchExecution entity** - All necessary fields for history tracking
- **batch_execution_history table** - Complete schema with proper indexes
- **MyBatis Mappings** - Full CRUD operations with paging support

**Architecture Decision: Dual-Layer Status Tracking**
1. In-memory CompletableFuture map for running tasks (fast polling)
2. Database persistence for complete history and recovery after restart
3. Automatic cleanup: Memory map removes entry after completion
4. Database serves as source of truth for completed/failed executions

**Security Implementation**
- User authentication required on all batch endpoints
- User isolation: History queries filtered by user ID
- CSRF protection on form submissions
- Authorization checks in REST controller

**Status: ✅ FULLY IMPLEMENTED AND INTEGRATED**

### 2025/10/19 - Batch Infrastructure Implementation & Memory Bank Update (午前)
- **BatchController.java**: Routes for batch UI pages at `/admin/batch/start`, `/user/batch/start`, `/admin/batch/history`, `/user/batch/history`
- **BatchService.java**: Service class created with @Service, @Slf4j, and @Transactional annotations
- **BatchConfig.java**: Configuration properties mapping batch job definitions from YAML
- **batch/config.yml**: Job definitions with wait_time_test and wait_time_error test jobs
- **UI Templates**: batch/start.html and batch/history.html templates created
- **Test Scripts**: wait_time.sh and wait_time.bat for batch execution testing

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

### Immediate: Batch History Search Frontend Implementation
1. **Search Form Page Creation**
   - URL: `/admin/batch/search` for admins, `/user/batch/search` for users
   - Form fields:
     - Job name: text input (optional)
     - Status: dropdown (RUNNING, COMPLETED_SUCCESS, FAILED) or All
     - Start date range: two `<input type="date">` fields
     - End date range: two `<input type="date">` fields
     - User selection: dropdown (admin only - fetch user list from API)
     - Search button: calls `/api/batch/history/search`

2. **Results Display**
   - Table showing search results with columns: Job Name, Status, User (admin only), Start Time, End Time, Exit Code
   - Status displayed with color-coded badges
   - Click to view details (optional for now)

3. **Pagination Controls**
   - Page size: 10 items per page (fixed)
   - Navigation: Previous button, page number display, Next button
   - All controls update based on totalPages from API response

4. **JavaScript Implementation**
   - Vanilla JavaScript (no framework)
   - Form validation before search
   - API call to `/api/batch/history/search`
   - DOM manipulation to display results
   - Clear search button to reset form

### Future: Logging System Implementation
1. **Structured JSON Logging**
   - Configure Logback with JSON output format
   - Add logstash-logback-encoder dependency (if needed)
   - Set up separate log levels for development/production

2. **Application Layer Logging**
   - Add @Slf4j to controllers and services
   - Log all user actions (batch execution, history views)
   - Include context: user ID, execution ID, timestamps

3. **Security Event Logging**
   - Login attempts (success/failure)
   - Authorization violations
   - Suspicious activities
   - Note: Never log passwords or sensitive data

4. **Error Tracking**
   - Log all exceptions with full context
   - Include user ID, request ID, operation type
   - Never expose stack traces to users

### Testing & Validation
1. **Manual Testing** - Test batch execution via UI
2. **Error Scenarios** - Test timeout, non-existent jobs, failed executions
3. **Pagination** - Verify page navigation works correctly
4. **Concurrent Execution** - Test multiple users executing batches simultaneously
5. **Database Persistence** - Verify history survives server restart

### Production Readiness
1. **Performance Tuning** - Connection pooling, query optimization
2. **Monitoring** - Batch execution metrics, performance metrics
3. **Backup Strategy** - Batch execution history backup
4. **Documentation** - User guide, API documentation

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
