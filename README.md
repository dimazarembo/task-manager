# Task Management REST API

Spring Boot 4 REST service for task management with JWT authentication.

## Stack

- Java 17+
- Spring Boot 4.0.3
- Spring Security + JWT (`java-jwt`)
- Spring Data JPA
- H2 (in-memory DB)
- Springdoc OpenAPI / Swagger UI
- JUnit 5 + Mockito

## Features

- User registration and login (JWT token)
- Task CRUD
- Task filters by:
  - status
  - assignee
  - author
- Role model: `USER`, `ADMIN`
- Access rules:
  - `POST /api/tasks` -> `USER`, `ADMIN`
  - `GET /api/tasks/{id}` -> `USER`, `ADMIN`
  - `GET /api/tasks` -> `USER`, `ADMIN`
  - `PUT /api/tasks/{id}` -> author or admin
  - `DELETE /api/tasks/{id}` -> author or admin

## Data Model

### User

- `id`
- `username` (unique)
- `email` (unique)
- `password` (BCrypt)
- `role` (`USER`, `ADMIN`)

### Task

- `id`
- `title` (required)
- `description`
- `status` (`TODO`, `IN_PROGRESS`, `DONE`)
- `priority` (`LOW`, `MEDIUM`, `HIGH`)
- `author` (required)
- `assignee` (nullable)
- `createdDate` (auto)
- `updatedAt` (auto)

## Configuration

Main config file: `src/main/resources/application.yaml`

Important properties:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:tm;MODE=PostgreSQL;DB_CLOSE_DELAY=-1

app:
  jwt:
    secret: secret
    ttl-hours: 24
  bootstrap:
    admin:
      enabled: true
      username: admin
      password: password
      email: admin@admin.com
```

`AdminBootstrap` creates admin user on startup when `app.bootstrap.admin.enabled=true`.

## Run

### Windows (PowerShell)

```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

### Linux/macOS

```bash
./mvnw clean compile
./mvnw spring-boot:run
```

Application URL:

- `http://localhost:8080`

## Swagger / OpenAPI

- UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

JWT is integrated with Swagger `Authorize` button.

Use flow:
1. Call `POST /api/auth/login`
2. Copy `token` from response
3. Click `Authorize` in Swagger
4. Paste token (without `Bearer ` prefix)

## H2 Console

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:tm`
- User: `sa`
- Password: empty

## API

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`

Example register:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "email": "user1@mail.com",
    "password": "password123"
  }'
```

Example login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password"
  }'
```

### Tasks

- `POST /api/tasks`
- `GET /api/tasks/{id}`
- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`
- `GET /api/tasks?status=IN_PROGRESS&assigneeId=1&authorId=2`

Create task example:

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Prepare report",
    "description": "Q4 report",
    "taskPriority": "HIGH",
    "assigneeId": 1
  }'
```

Update task example:

```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Prepare report v2",
    "description": "Q4 report updated",
    "taskStatus": "IN_PROGRESS",
    "taskPriority": "MEDIUM",
    "assigneeId": 1
  }'
```

## Tests

Run all tests:

### Windows

```powershell
.\mvnw.cmd test
```

### Linux/macOS

```bash
./mvnw test
```

Current test types:

- Unit tests (JUnit 5 + Mockito):
  - `TaskServiceTest`
  - `AuthServiceTest`
- Integration test:
  - `AuthControllerIntegrationTest` (`POST /api/auth/login`)

## Docker

Docker is not required for current project setup because H2 in-memory database is used.
Reviewer can run everything with Maven only.

## Notes

- If protected endpoint returns `403`, check:
  - `Authorization` header exists
  - header format is `Bearer <token>`
- JWT contains claims:
  - `sub` (username)
  - `role`
  - `iat`, `exp`
