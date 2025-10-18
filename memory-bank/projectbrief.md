# Project Brief

## Project Name
Training Spring Boot Application

## Project Type
Web Application - Internal Training/Defense Industry System

## Core Purpose
A role-based access control web application built with Spring Boot, demonstrating enterprise-level authentication, authorization, and batch processing capabilities for defense industry use cases.

## Primary Goals

1. **Authentication & Authorization**
   - Implement secure user login/logout functionality
   - Support role-based access control (ADMIN, USER)
   - Provide separate home screens based on user roles

2. **Batch Processing Management**
   - Create a batch execution interface
   - Enable asynchronous batch program execution
   - Monitor and display batch execution status in real-time
   - Maintain execution history with pagination

3. **System Logging**
   - Implement comprehensive logging functionality
   - Support structured JSON logging
   - Track user actions and system events

## Key Requirements

### Functional Requirements
1. Login and logout functionality
2. Role-based home screen differentiation (Admin vs User)
3. Logging implementation with proper levels (DEBUG, INFO, WARN, ERROR, FATAL)
4. Batch execution interface
   - Execute programs from configured directory asynchronously
   - Status polling via REST API (every 5 seconds)
   - Display status: Running, Completed Successfully, Failed [exit code]
5. Batch execution history display
6. Pagination for execution history

### Non-Functional Requirements
- Security: Follow secure coding principles
- Code Quality: Apply DDD, TDD, and FP principles
- Architecture: Separation of concerns, high cohesion, loose coupling
- Documentation: All code comments and documentation in English

## Technology Stack
- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **ORM**: MyBatis 3.0.3
- **Template Engine**: Thymeleaf
- **Security**: Spring Security
- **Database**: H2 (development), configurable for production
- **Build Tool**: Maven
- **Code Generation**: Lombok

## Success Criteria
- Secure authentication and authorization working correctly
- Role-based access control properly implemented
- Batch execution and monitoring system functional
- Comprehensive logging in place
- All features tested and documented
- Code follows established coding standards and security guidelines
