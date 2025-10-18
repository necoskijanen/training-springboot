# System Patterns

## Architecture Overview

### Layered Architecture
The application follows a traditional layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Controllers, Thymeleaf Templates)     │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│         Security Layer                  │
│  (Spring Security, Custom Handlers)     │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│         Application Layer               │
│  (Controllers, Service Logic)           │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│         Domain Layer                    │
│  (Entities, Value Objects)              │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│         Data Access Layer               │
│  (MyBatis Mappers)                      │
└─────────────────────────────────────────┘
```

## Key Design Patterns

### 1. Repository Pattern
- **Implementation**: MyBatis Mappers
- **Purpose**: Abstract database access
- **Files**:
  - `UserMapper.java` - Interface defining data operations
  - `UserMapper.xml` - SQL mapping implementation

### 2. Domain Model Pattern
- **Entities**: `User`, `Role`
- **Characteristics**:
  - Use Lombok for boilerplate reduction
  - Contain business logic where appropriate
  - Represent core domain concepts

### 3. Service Layer Pattern (Future)
- **Purpose**: Encapsulate business logic
- **Location**: `com.example.demo.service` (to be created)
- **Responsibility**: Coordinate between controllers and repositories

### 4. Custom Authentication Handler
- **Pattern**: Strategy Pattern
- **Implementation**: `CustomAuthenticationSuccessHandler`
- **Purpose**: Role-based redirect after successful authentication
- **Behavior**:
  - ADMIN role → `/admin/home`
  - USER role → `/user/home`

### 5. MVC Pattern
- **Model**: Domain entities and data
- **View**: Thymeleaf templates
- **Controller**: Spring MVC controllers
  - `MvcController` - Login page routing
  - `AdminController` - Admin-specific pages
  - `UserController` - User-specific pages

## Component Relationships

### Security Flow
```
Login Request
    ↓
Spring Security Filter Chain
    ↓
CustomUserDetailsService
    ↓
UserMapper (Database Query)
    ↓
Authentication Success/Failure
    ↓
CustomAuthenticationSuccessHandler
    ↓
Role-Based Redirect
```

### Data Access Flow
```
Controller
    ↓
Service (Future Layer)
    ↓
MyBatis Mapper Interface
    ↓
MyBatis XML Mapping
    ↓
Database (H2)
```

## Database Schema Design

### Entity-Relationship Model
```
user_master (1) ←→ (N) user_role (N) ←→ (1) role_definition

user_master:
- id (PK)
- name
- email (UNIQUE)
- password (BCrypt hashed)
- is_active
- created_at
- updated_at

role_definition:
- id (PK)
- name (UNIQUE)

user_role:
- user_id (FK, PK)
- role_id (FK, PK)
```

### Design Decisions
- **Many-to-Many Relationship**: User-Role relationship uses intermediate table
- **Soft Delete Ready**: `is_active` flag on user_master
- **Audit Fields**: `created_at`, `updated_at` for tracking
- **Cascade Delete**: Removing a user cascades to user_role entries

## Security Architecture

### Authentication
- **Framework**: Spring Security
- **Password Encoding**: BCrypt
- **User Details**: Custom implementation via `CustomUserDetailsService`
- **Session Management**: Default Spring Security session handling

### Authorization
- **Method**: Role-based access control (RBAC)
- **Roles**: ADMIN, USER
- **URL Protection**:
  - `/admin/**` → ROLE_ADMIN only
  - `/user/**` → ROLE_ADMIN or ROLE_USER
  - `/login`, static resources → permit all
  - All other URLs → authenticated users only

### Configuration
- **File**: `SecurityConfig.java`
- **Key Beans**:
  - `PasswordEncoder` - BCryptPasswordEncoder
  - `AuthenticationSuccessHandler` - Custom role-based redirect
  - `SecurityFilterChain` - URL authorization rules

## Coding Principles Applied

### Separation of Concerns
- **Controllers**: Handle HTTP requests/responses only
- **Security**: Isolated in security package
- **Domain**: Pure domain logic without framework dependencies
- **Mappers**: Database access abstraction

### High Cohesion, Loose Coupling
- Interfaces define contracts (e.g., UserMapper)
- Implementations are swappable
- Domain entities are framework-agnostic

### Dependency Injection
- Spring manages all component lifecycle
- Constructor injection preferred (when implemented)
- @Autowired for field injection currently used

## Future Architectural Additions

### Batch Processing Layer
```
┌─────────────────────────────────────────┐
│         REST API Controllers            │
│  (Batch Status, History)                │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│         Batch Service Layer             │
│  (Execution, Monitoring)                │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│         Process Manager                 │
│  (Async Execution, Status Tracking)     │
└─────────────────────────────────────────┘
```

### Logging Infrastructure
- Structured JSON logging
- Separate logger for security events
- Request/Response logging with correlation IDs
- Error tracking with full context

### Planned Patterns
- **Command Pattern**: For batch execution requests
- **Observer Pattern**: For status change notifications
- **Factory Pattern**: For creating batch execution contexts
