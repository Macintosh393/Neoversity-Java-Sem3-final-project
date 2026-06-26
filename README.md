# University Management System (UMS) REST API

RESTful API backend for a University Management System built using Spring Boot 3, Spring Data JPA, and PostgreSQL.

## Features

- **Entity Management (CRUD)**: Complete endpoints for Students, Teachers, and Courses.
- **Enrollment Flow**: Enroll students into courses, grade their performance, and record payments.
- **Reporting & Statistics**:
  - GPA calculation (weighted by ECTS course credits).
  - Detailed student transcripts.
  - Course/Semester average GPA calculation.
  - Search by name or email.
  - Filtering by status/credits/teachers.
  - Top-N students list based on GPA.
  - List of students with unpaid courses.
- **API Documentation**: Interactive Swagger UI through Springdoc OpenAPI.
- **Centralized Exception Handling**: Global controller advice returning clean JSON error responses with proper HTTP status codes.

---

## Technology Stack

- **Java**: 21+
- **Framework**: Spring Boot 3+ (JPA, Web, Validation)
- **Database**: PostgreSQL (Production/Dev), H2 In-Memory (Testing)
- **APIs & Docs**: Springdoc OpenAPI / Swagger UI
- **Testing**: JUnit 5, Mockito, Spring Boot Starter Test

---

## Getting Started

### 1. Prerequisites
- Java 21 installed.
- Docker & Docker Compose installed (optional, to run PostgreSQL).

### 2. Run Database
Start the PostgreSQL instance locally using Docker Compose:
```bash
docker-compose up -d
```
This boots up a PostgreSQL container on port `5432` with a database named `university_db`.

### 3. Run the Application
Start the Spring Boot server:
```bash
./gradlew bootRun
```
The server will run on `http://localhost:8080`.

### 4. Interactive API Documentation (Swagger)
Open your browser and navigate to:
```text
http://localhost:8080/swagger-ui.html
```
Here you can explore and execute all the REST API endpoints.

---

## Running Tests
Run the unit and integration test suite:
```bash
./gradlew test
```
All integration tests run against an in-memory H2 database using the `test` profile.
