# Technical Context

## Technology Stack

### Core Framework
- **Spring Boot**: 3.5.6
  - Spring Boot Starter Web
  - Spring Boot Starter Security
  - Spring Boot Starter Test
- **Java Version**: 21
- **Build Tool**: Maven

### Key Dependencies

#### Persistence Layer
- **MyBatis Spring Boot Starter**: 3.0.3
  - ORM framework for SQL mapping
  - XML-based query configuration
  - Type-safe mapper interfaces

#### Database
- **H2 Database**: In-memory database
  - Scope: runtime
  - Used for development and testing
  - Mode: `jdbc:h2:mem:devdb`
  - Auto-initialization enabled

#### Security
- **Spring Security**: (managed by Spring Boot parent)
  - Authentication and authorization
  - BCrypt password encoding
  - Role-based access control
- **Spring Security Test**: For testing security features

#### Template Engine
- **Thymeleaf**: (managed by Spring Boot parent)
  - Server-side template rendering
  - Natural templating
- **Thymeleaf Spring Security 6 Extras**: Integration with Spring Security

#### Code Generation
- **Lombok**: (managed by Spring Boot parent)
  - Reduces boilerplate code
  - Generates getters, setters, constructors
  - @Data, @Builder annotations

## Development Environment

### Build Configuration
- **Maven Wrapper**: Included (mvnw, mvnw.cmd)
- **Java Compiler**: Target Java 21
- **Source Encoding**: UTF-8 (implied)

### Application Configuration

#### Development Profile (`application.yml`)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:devdb
    driver-class-name: org.h2.Driver
    username: sa
    password: ''
  sql.init.mode: always

mybatis:
  mapper-locations: classpath:mapper/*.xml
```

#### Production Profile (`application-prod.yml`)
- Placeholder for production database configuration
- Should use persistent database (PostgreSQL, MySQL, etc.)

### Project Structure
```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── Main.java
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── AdminController.java
│   │   │   ├── MvcController.java
│   │   │   └── UserController.java
│   │   ├── domain/
│   │   │   ├── Role.java
│   │   │   └── User.java
│   │   ├── mapper/
│   │   │   ├── UserMapper.java
│   │   │   └── UserMapper.xml
│   │   └── security/
│   │       ├── CustomAuthenticationSuccessHandler.java
│   │       └── CustomUserDetailsService.java
│   └── resources/
│       ├── application.yml
│       ├── application-prod.yml
│       ├── schema.sql
│       ├── data.sql
│       ├── static/
│       └── templates/
│           ├── login.html
│           ├── admin/
│           │   └── home.html
│           └── user/
│               └── home.html
└── test/
    └── java/
        ├── PasswordEncoderTest.java
        └── com/example/demo/
            └── LoginTest.java
```

## Technical Constraints

### Language Requirements
- All source code comments must be in English
- Variable and method names in English
- Documentation in English

### Security Requirements
- All passwords must be BCrypt hashed
- No plain text password storage
- Session management via Spring Security
- CSRF protection enabled (default)

### Coding Standards
- Follow functional programming principles where applicable
- Use immutable data structures when possible
- Prefer constructor injection over field injection
- Write self-documenting code with clear method names

### Database Constraints
- Development uses H2 in-memory database
- Schema must support production migration
- Use prepared statements (MyBatis handles this)
- Follow normalization principles

## Development Workflow

### Running the Application
```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Testing
```bash
# Run all tests
./mvnw test

# Run specific test
./mvnw test -Dtest=LoginTest
```

### Database Initialization
- Schema: Automatically loaded from `schema.sql` on startup
- Data: Automatically loaded from `data.sql` on startup
- Mode: `spring.sql.init.mode: always`

## Future Technical Additions

### Logging Framework
- **Logback**: (included with Spring Boot)
- **SLF4J**: API for structured logging
- Configuration: `logback-spring.xml` (to be created)
- Format: JSON for production, console for development

### Batch Processing
- **Spring @Async**: For asynchronous execution
- **ProcessBuilder**: For external program execution
- **ExecutorService**: Thread pool management
- **CompletableFuture**: Status tracking

### REST API
- **Spring Web**: Already included
- **@RestController**: For batch status endpoints
- **ResponseEntity**: For proper HTTP responses
- **Jackson**: JSON serialization (included)

### Pagination
- **Spring Data Commons**: Pageable interface
- **MyBatis PageHelper**: Or manual LIMIT/OFFSET
- Frontend: JavaScript for AJAX pagination

### Additional Dependencies (To Be Added)
```xml
<!-- For structured logging -->
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>

<!-- For production database (example: PostgreSQL) -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

## IDE and Tools

### Supported IDEs
- Visual Studio Code (current)
- IntelliJ IDEA
- Eclipse

### Available CLI Tools
- git - Version control
- gh - GitHub CLI
- docker - Containerization
- curl - API testing
- maven - Build tool (via wrapper)

### Recommended VS Code Extensions
- Extension Pack for Java
- Spring Boot Extension Pack
- Lombok Annotations Support
- Database Client (for H2 console)

## Performance Considerations

### Current State
- In-memory H2 database (fast but non-persistent)
- No connection pooling configured
- Default Spring Boot settings

### Future Optimizations
- Connection pooling for production database (HikariCP)
- Query optimization and indexing
- Caching layer (Spring Cache with Redis)
- Async processing for batch operations
- Thread pool tuning for concurrent batch executions
