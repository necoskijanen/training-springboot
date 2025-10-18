# Principles

- All descriptions in source files must be in English.
- Comments
  - Explain complex logic.
  - Provide reasons for avoiding commonly used algorithms.
- Ensure each component follows separation of concerns, high cohesion, and loose coupling.
- Domain logic should depend on interfaces rather than framework or library details.
- Follow the conventions and best practices of the programming language being used.

## Functional Programming (FP)

- Prioritize pure functions.
- Use immutable data structures.
- Isolate side effects.
- Ensure type safety.

## Domain-Driven Design (DDD)

- Distinguish between value objects and entities.
- Maintain consistency within aggregates.
- Abstract data access using repositories.
- Define and respect bounded contexts.

## Test-Driven Development (TDD)

- Follow the Red-Green-Refactor cycle.
- Treat tests as specifications.
- Iterate in small increments.
- Continuously refactor.

## Algorithms

- Use stream processing when data volume is unclear.
- Choose the option with the lowest computational complexity when multiple candidates exist.
- If optimizing computational complexity requires excessive memory usage, confirm with the user.

# Implementation Patterns

- Value Object
  - Immutable.
  - Identity based on value.
  - Self-validating.
  - Contains domain operations.
- Entity
  - Identity based on an ID.
  - Controlled updates.
  - Maintains consistency rules.
- Error Handling
  - Explicitly indicate success/failure.
  - Use early return patterns.
  - Define error types.
- Repository
  - Handles only domain models.
  - Hides persistence details.
  - Provides an in-memory implementation for testing.
- Adapter Pattern
  - Abstracts external dependencies.
  - Caller defines the interface.
  - Easily replaceable in tests.

# Implementation Process

1. Type Design

- Define types first.
- Represent domain concepts with types.

2. Implement Pure Functions First

- Start with functions that have no external dependencies.
- Write tests first.

3. Separate Side Effects

- Push I/O operations to function boundaries.
- Wrap side-effect-heavy operations in Promises.

4. Implement Adapters

- Abstract access to external services and databases.
- Provide mocks for testing.

# Logging

## Principles

- Output structured logs in JSON format.
- Backend Logging
  - Include error causes, locations, and context information (e.g., user ID, request ID, session ID).
  - Log details of crafted requests that are impossible in normal UI interactions.
  - Never log personal information.
- CLI Tool Logging
  - Include error causes, locations, and necessary user actions.

## Log Levels

- DEBUG
  - Provides detailed debug information.
  - Used in development, disabled in production.
- INFO
  - Records normal operations and user actions.
  - Used to track system health.
- WARN
  - Indicates potential issues or situations requiring attention.
  - Logs events that are not currently critical but may become problems later.
- ERROR
  - Indicates critical problems affecting users.
  - Logs errors with appropriate context information.
- FATAL
  - Represents severe system-wide failures.
  - Used for critical issues requiring immediate attention.
