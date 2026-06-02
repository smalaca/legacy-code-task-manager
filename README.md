[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=smalaca_legacy-code-task-manager&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=smalaca_legacy-code-task-manager)

# Legacy Code Task Manager

## Purpose
Are you looking for a place to practice refactoring techniques, architectural re-design, or to test new ways of handling legacy systems? If so, this project is designed specifically for you. It serves as a playground for developers who want to improve their skills in transforming "ugly" code into something clean and maintainable.

This is not intended to be a reference for well-written code. On the contrary, it is an intentional example of how NOT to design a system and how NOT to write tests. It provides a non-trivial codebase that is large enough to demonstrate real-world coupling and boundary issues, yet small enough to be manageable for training purposes.

If you are looking for an example of well-designed microservices, you might want to check out [Training Center Microservices](https://github.com/smalaca/training-center-microservices).

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