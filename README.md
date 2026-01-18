# Task Management System

A RESTful API for managing tasks, built with Spring Boot following Domain-Driven Design (DDD) principles, Test-Driven Development (TDD), and clean coding practices.

## Features

- **CRUD Operations**: Create, Read, Update, and Delete tasks
- **Task Filtering**: Filter tasks by status (PENDING, IN_PROGRESS, DONE) with database-level filtering
- **Pagination**: Efficient database-level pagination for large datasets
- **Validation**: Comprehensive input validation including custom future date validation
- **Error Handling**: Proper HTTP status codes and error messages
- **H2 Database**: Uses H2 in-memory database with JPA for data persistence
- **Swagger UI**: Automatic API documentation with interactive testing
- **Lombok**: Reduces boilerplate code for cleaner implementation

## Requirements

- **Java 21** or higher
- **Maven 3.6+** (or use the included Maven Wrapper)

## Installation

### Prerequisites

1. **Java 21**: Ensure Java 21 is installed on your system
   ```bash
   java -version
   ```

2. **Maven**: Maven is included via the Maven Wrapper (`mvnw`), so no separate installation is needed.

### Setup Steps

1. **Clone or extract the project** to your desired location

2. **Navigate to the project directory**:
   ```bash
   cd TaskManagementSystem
   ```

3. **Build the project**:
   ```bash
   ./mvnw clean install
   ```
   On Windows:
   ```bash
   mvnw.cmd clean install
   ```

4. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```
   On Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

5. The application will start on `http://localhost:8080`

## Configuration

### H2 Database Console

The H2 database console is enabled for development and testing purposes. You can access it at:

```
http://localhost:8080/h2-console
```

**Connection Details:**
- **JDBC URL**: `jdbc:h2:mem:taskdb`
- **Username**: `sa`
- **Password**: (leave empty)

**Note**: The database is in-memory, so data will be lost when the application stops.

### Swagger UI

Swagger UI is automatically available for API documentation and testing:

```
http://localhost:8080/swagger-ui.html
```

**Features:**
- Interactive API documentation
- Try out endpoints directly from the browser
- View request/response schemas
- Test with different parameters

**OpenAPI JSON:**
```
http://localhost:8080/v3/api-docs
```

## Running Tests

To run all tests:
```bash
./mvnw test
```

On Windows:
```bash
mvnw.cmd test
```

## API Endpoints

### Base URL
```
http://localhost:8080
```

### 1. Create Task
**POST** `/tasks`

**Request Body:**
```json
{
  "title": "Complete project documentation",
  "description": "Write comprehensive documentation for the project",
  "status": "PENDING",
  "dueDate": "2024-12-31"
}
```

**Response:** `201 Created`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Complete project documentation",
  "description": "Write comprehensive documentation for the project",
  "status": "PENDING",
  "dueDate": "2024-12-31"
}
```

**Validation:**
- `title` (required): Must not be blank
- `dueDate` (required): Must be a valid date in the future
- `description` (optional): Can be null or empty
- `status` (optional): Defaults to `PENDING` if not provided

### 2. Get Task by ID
**GET** `/tasks/{id}`

**Response:** `200 OK`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Complete project documentation",
  "description": "Write comprehensive documentation for the project",
  "status": "PENDING",
  "dueDate": "2024-12-31"
}
```

**Error Response:** `404 Not Found`
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Task Not Found",
  "message": "Task with id 550e8400-e29b-41d4-a716-446655440000 not found"
}
```

### 3. Update Task
**PUT** `/tasks/{id}`

**Request Body:**
```json
{
  "title": "Updated title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "dueDate": "2025-01-15"
}
```

**Note:** All fields are optional in the update request. Only provided fields will be updated.

**Response:** `200 OK`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Updated title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "dueDate": "2025-01-15"
}
```

**Error Response:** `404 Not Found` (if task doesn't exist)

### 4. Delete Task
**DELETE** `/tasks/{id}`

**Response:** `204 No Content`

**Error Response:** `404 Not Found` (if task doesn't exist)

### 5. List All Tasks
**GET** `/tasks`

**Query Parameters:**
- `status` (optional): Filter by status (`PENDING`, `IN_PROGRESS`, `DONE`)
- `page` (optional, default: 0): Page number (0-indexed)
- `size` (optional, default: 100): Number of items per page

**Examples:**
- Get all tasks: `GET /tasks`
- Filter by status: `GET /tasks?status=PENDING`
- Pagination: `GET /tasks?page=0&size=10`
- Combined: `GET /tasks?status=IN_PROGRESS&page=0&size=5`

**Response:** `200 OK`
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "Task 1",
    "description": "Description 1",
    "status": "PENDING",
    "dueDate": "2024-12-25"
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "title": "Task 2",
    "description": "Description 2",
    "status": "IN_PROGRESS",
    "dueDate": "2024-12-26"
  }
]
```

**Note:** 
- Tasks are automatically sorted by `dueDate` in ascending order
- **Database-level pagination**: Only the requested page is fetched from the database for optimal performance

## Task Status Values

- `PENDING`: Task is not yet started (default)
- `IN_PROGRESS`: Task is currently being worked on
- `DONE`: Task has been completed

## Error Responses

All error responses follow this format:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "{fieldName=error message}"
}
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/indiasatcom/TaskManagementSystem/
│   │       ├── controller/          # REST API endpoints
│   │       ├── domain/              # Domain entities and enums
│   │       ├── dto/                  # Data Transfer Objects
│   │       ├── exception/            # Custom exceptions and handlers
│   │       ├── repository/           # Data access layer (JPA)
│   │       ├── service/              # Business logic layer
│   │       └── validation/            # Custom validators
│   └── resources/
│       └── application.properties    # Application configuration
└── test/
    └── java/
        └── com/indiasatcom/TaskManagementSystem/
            ├── controller/           # Integration tests
            ├── repository/           # Repository unit tests
            └── service/              # Service unit tests
```

## Design Principles

### Domain-Driven Design (DDD)
- **Domain Layer**: Core business entities (`Task`, `TaskStatus`)
- **Repository Layer**: Data persistence abstraction using Spring Data JPA
- **Service Layer**: Business logic and orchestration
- **Controller Layer**: HTTP request/response handling

### Test-Driven Development (TDD)
- Unit tests for repository and service layers
- Integration tests for all REST endpoints
- Mock dependencies for isolated testing

### Clean Code Practices
- Meaningful variable and method names
- Single Responsibility Principle
- Interface-based design for testability
- Comprehensive error handling
- Input validation
- Lombok for reducing boilerplate

## Performance Optimizations

### Database-Level Pagination
The application uses Spring Data JPA's `Pageable` for efficient database queries:
- **Before**: Fetched all records, then filtered/paginated in memory
- **After**: Database handles filtering, sorting, and pagination
- **Benefit**: Only requested page size is fetched (e.g., 10 records instead of 1000)

### Query Optimization
- Status filtering happens at the database level using `WHERE` clause
- Sorting happens at the database level using `ORDER BY`
- Pagination uses `LIMIT` and `OFFSET` at the database level

## Example Usage

### Using cURL

**Create a task:**
```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Spring Boot",
    "description": "Complete Spring Boot tutorial",
    "status": "PENDING",
    "dueDate": "2024-12-31"
  }'
```

**Get all tasks:**
```bash
curl http://localhost:8080/tasks
```

**Get task by ID:**
```bash
curl http://localhost:8080/tasks/{task-id}
```

**Update a task:**
```bash
curl -X PUT http://localhost:8080/tasks/{task-id} \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated title",
    "status": "IN_PROGRESS"
  }'
```

**Delete a task:**
```bash
curl -X DELETE http://localhost:8080/tasks/{task-id}
```

**Filter by status:**
```bash
curl http://localhost:8080/tasks?status=PENDING
```

**Pagination:**
```bash
curl http://localhost:8080/tasks?page=0&size=10
```

**Filter with pagination:**
```bash
curl http://localhost:8080/tasks?status=PENDING&page=0&size=5
```

## Custom Validation

### @FutureDate Annotation

The application includes a custom validation annotation `@FutureDate` that ensures dates are in the future:

```java
@FutureDate(message = "Due date must be a valid date in the future")
private LocalDate dueDate;
```

**How it works:**
- Validates that the date is after today
- Returns `true` for null values (lets `@NotNull` handle null validation)
- Uses `LocalDate.isAfter(LocalDate.now())` for comparison

## Testing

The project includes comprehensive test coverage:

- **Unit Tests**: Test individual components in isolation
  - Repository tests using `@DataJpaTest`
  - Service tests with mocked dependencies
  
- **Integration Tests**: Test end-to-end API functionality
  - Full Spring context with `@SpringBootTest`
  - Tests all REST endpoints

Run tests with:
```bash
./mvnw test
```

## Technologies Used

- **Spring Boot 3.5.9**: Application framework
- **Java 21**: Programming language
- **Spring Data JPA**: Data persistence layer
- **H2 Database**: In-memory database
- **SpringDoc OpenAPI 2.3.0**: API documentation (Swagger)
- **Maven**: Build tool
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework
- **Jakarta Validation**: Input validation
- **Lombok**: Reduces boilerplate code

## License

This project is part of a coding challenge.

## Author

Task Management System - Coding Challenge Implementation

