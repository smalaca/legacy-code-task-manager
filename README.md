[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=smalaca_legacy-code-task-manager&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=smalaca_legacy-code-task-manager)

# Legacy Code Task Manager

## Purpose
Most developers never get a realistic environment to practice refactoring or architecture evolution. Toy examples are too small, and production systems are too risky. This repository is intentionally designed as a painful legacy system playground to bridge that gap.

It is large enough to practice:
*   **Refactoring Techniques**: Improving code quality without changing behavior.
*   **Rearchitecture Strategies**: Moving from a "Big Ball of Mud" to a more structured design.
*   **Legacy Code Modernization**: Updating old patterns to modern standards.
*   **Testing Improvements**: Transforming fragile, implementation-coupled tests into valuable behavior-focused ones.
*   **Modularization**: Introducing and enforcing better architectural boundaries.

### Looking for an Example of Well-Designed Microservices?
If you are looking for an example of well-designed microservices, you might want to check out [Training Center Microservices](https://github.com/smalaca/training-center-microservices).

## How to Start
If you want to use this repository to upskill yourself, pay attention to the testing strategy:
*   **Support for Refactoring**: Tests in the package `com.smalaca.taskamanager.api.rest` are written in a way that supports refactoring and redesign. When they fail, you can safely assume you broke something.
*   **Implementation-Coupled Tests**: Almost all other tests are tightly coupled to the implementation. Relying on them during refactoring is difficult as they often fail due to changes in internal structure rather than broken behavior.

### Good vs. Bad Tests Comparison
To see the difference between tests that support refactoring and those that hinder it, compare:
*   `UserControllerTest`: Focuses on behavior, uses an in-memory repository, and is not fragile.
*   `UserControllerMockTest`: Focuses on implementation details, relies heavily on mocks, and is very fragile.

Try performing a few refactorings in `UserController` and observe which tests fail and why. You'll notice that `UserControllerMockTest` often fails because it "tests" how the code is written, while `UserControllerTest` remains green as long as the behavior is preserved.

## Domain Perspective
The project covers a classic Task Management domain, supporting Agile-like workflows. It includes entities such as:
*   **Projects**: High-level containers for all work.
*   **Epics, Stories, and Tasks**: A hierarchical structure for managing requirements and work items.
*   **Sprints**: Time-boxed iterations for work execution.
*   **Teams and Users**: Management of people, their roles, and assignments.

## Tech Stack
The application is built using a modern Java stack:
*   **Language**: Java 21
*   **Framework**: Spring Boot 3.2.5
*   **Data Access**: Spring Data JPA with H2 in-memory database
*   **Tools**: Lombok, Apache Commons Lang3, Google Guava
*   **Build Tool**: Maven

## Design
The current design is a classic example of a "Big Ball of Mud":
*   **High Coupling**: Logic is scattered and heavily interdependent.
*   **Anemic Domain Model**: Entities are often used just as data containers, while logic resides in the web layer.
*   **Lack of Boundaries**: There are no clear separations between the API, business logic, and infrastructure layers.
*   **Direct Repository Access**: REST controllers directly interact with JPA repositories and handle complex mapping logic.

## Testing
The project boasts a high test coverage (over 95%), but the tests are designed to be "useless" from a maintenance perspective:
*   **Fragile Tests**: Tests are tightly coupled to the implementation details.
*   **Low Expressiveness**: It is difficult to understand what business requirements are being verified.
*   **Technical Focus**: Most tests focus on the technical aspects of the infrastructure rather than business outcomes.
*   **Verification Strategy**: Uses JUnit 5, Mockito, and AssertJ, but often in ways that make refactoring harder instead of easier.

## Collaboration
Feel free to fork it, raise issues, or open pull requests. Just remember: **the goal of this repository is to 
stay bad and underdesigned on purpose.**