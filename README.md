# Market Local Shops Backend (Spring Boot)

This is the newly rewritten Java Spring Boot backend for the Namma Market project.

## Tech Stack
- Java 21
- Spring Boot 3.4.x
- Spring Security + JWT
- PostgreSQL + Spring Data JPA
- Flyway Migrations
- Lombok + MapStruct
- Swagger/OpenAPI

## Setup

1. Run dependencies (PostgreSQL):
   ```bash
   docker-compose up -d db
   ```

2. Build and run the app:
   ```bash
   ./mvnw spring-boot:run
   ```

3. View Swagger Docs:
   `http://localhost:8080/swagger-ui.html`
