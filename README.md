# Student Management REST API

A Spring Boot REST API for managing student records, backed by MySQL. It started as a Python + MySQL project and was rebuilt in Java with a layered architecture, input validation, and proper error handling.

**Stack:** Java 17 · Spring Boot 3.5 · Spring Web · Spring Data JPA (Hibernate) · Bean Validation · MySQL 8 · Maven

## Endpoints

| Method | Path | What it does | Success |
|---|---|---|---|
| GET | `/api/students` | List students (paginated). Optional filters: `department`, `name` | 200 |
| GET | `/api/students/{id}` | Get one student | 200 |
| POST | `/api/students` | Create a student | 201 + `Location` header |
| PUT | `/api/students/{id}` | Update a student | 200 |
| DELETE | `/api/students/{id}` | Delete a student | 204 |

Paging and sorting: `?page=0&size=10&sort=cgpa,desc`

Errors come back as JSON: **400** (validation or bad input), **404** (not found), **409** (duplicate email).

## Run it

1. Install **JDK 17+** and **MySQL 8**, and make sure MySQL is running.
2. Open `src/main/resources/application.properties` and put your MySQL password in place of `root` (or set the `DB_PASSWORD` environment variable). The `student_db` database is created automatically.
3. Start the app:
   - **IntelliJ IDEA:** open this folder, let Maven finish importing, then run `StudentApiApplication`.
   - **Terminal (Maven installed):** `mvn spring-boot:run`
4. The API is now at `http://localhost:8080/api/students`.

## Try it (Git Bash, Linux or macOS; Postman works too)

```bash
# Create
curl -i -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"name":"Anu Joseph","email":"anu@example.com","department":"CSE","semester":7,"cgpa":8.4}'

# List, filter, sort
curl "http://localhost:8080/api/students?department=cse&sort=cgpa,desc"

# Get, update, delete
curl http://localhost:8080/api/students/1
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Anu Joseph","email":"anu@example.com","department":"CSE","semester":8,"cgpa":8.6}'
curl -i -X DELETE http://localhost:8080/api/students/1

# Error cases
curl -i -X POST http://localhost:8080/api/students -H "Content-Type: application/json" \
  -d '{"name":"","email":"not-an-email","department":"CSE","semester":12}'   # 400 + field errors
curl -i http://localhost:8080/api/students/999                                # 404
```

Example validation error:

```json
{
  "timestamp": "2026-09-22T15:04:05Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/students",
  "fieldErrors": {
    "name": "name is required",
    "email": "email must be a valid address",
    "semester": "semester must be between 1 and 8"
  }
}
```

## How it works

- **Layered design:** Controller (HTTP) → Service (business rules, transactions) → Repository (Spring Data JPA) → MySQL.
- **DTO + validation:** requests are bound to the `StudentRequest` record and checked with `@Valid`. The `Student` entity is never bound straight from JSON, so clients can't set `id` or `createdAt`.
- **Business rules:** emails are normalised to lowercase and must be unique (checked in the service and also enforced by a unique DB constraint); departments are stored in uppercase.
- **Error handling:** `GlobalExceptionHandler` (`@RestControllerAdvice`) maps exceptions to one consistent JSON shape with 400/404/409.
- **Pagination:** Spring Data `Pageable`, returned through a small `PageResponse` DTO so the JSON shape stays stable.
- **Schema:** Hibernate creates/updates the `students` table from the entity (`ddl-auto=update`). Fine for development; a production app would use migrations (Flyway/Liquibase).

## Project structure

```
src/main/java/com/adhil/studentapi/
├── StudentApiApplication.java
├── controller/StudentController.java
├── service/StudentService.java
├── repository/StudentRepository.java
├── model/Student.java
├── dto/StudentRequest.java, PageResponse.java
└── exception/GlobalExceptionHandler.java, ApiError.java,
              ResourceNotFoundException.java, DuplicateResourceException.java
```
