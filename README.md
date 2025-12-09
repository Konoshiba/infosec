# Secure Web API Application

A secure Spring Boot REST API application with JWT authentication, PostgreSQL database, and protection against common security vulnerabilities.

## Features

- **JWT Authentication**: Secure token-based authentication
- **Password Hashing**: BCrypt password encryption
- **SQL Injection Protection**: Parameterized queries via Spring Data JPA
- **XSS Protection**: Input sanitization for all user data
- **PostgreSQL Database**: Persistent data storage

## API Endpoints

### 1. POST /auth/login
Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
  "username": "user123",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "user123"
}
```

### 2. GET /api/data
Returns a list of users. Requires authentication.

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "message": "List of users retrieved successfully",
  "users": [
    {
      "id": 1,
      "username": "user123",
      "email": "user@example.com",
      "fullName": "John Doe"
    }
  ],
  "totalCount": 1
}
```

### 3. GET /users/{id}
Returns a specific user by ID. Requires authentication.

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "id": 1,
  "username": "user123",
  "email": "user@example.com",
  "fullName": "John Doe"
}
```

## Security Features

### SQL Injection Protection
- All database queries use Spring Data JPA with parameterized queries
- No string concatenation for SQL queries
- Prepared statements are used automatically

### XSS Protection
- All user input is sanitized using Apache Commons Text
- HTML entities are escaped in responses
- JSON responses are sanitized

### Authentication Security
- Passwords are hashed using BCrypt
- JWT tokens are used for stateless authentication
- Tokens expire after 24 hours (configurable)
- Protected endpoints require valid JWT token

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### Database Setup
1. Create a PostgreSQL database:
```sql
CREATE DATABASE infosec_db;
```

2. Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/infosec_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Running the Application
1. Build the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## Testing the API

### 1. Create a test user (you can use a database client or add a data initializer)

### 2. Login to get a token:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'
```

### 3. Use the token to access protected endpoints:
```bash
curl -X GET http://localhost:8080/api/data \
  -H "Authorization: Bearer <your_token>"
```

```bash
curl -X GET http://localhost:8080/users/1 \
  -H "Authorization: Bearer <your_token>"
```

## Project Structure

```
src/
├── main/
│   ├── java/com/infosec/
│   │   ├── controller/     # REST controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access layer
│   │   ├── entity/         # JPA entities
│   │   ├── dto/            # Data transfer objects
│   │   ├── security/       # Security configuration
│   │   ├── util/           # Utility classes (JWT, XSS)
│   │   └── exception/      # Exception handlers
│   └── resources/
│       └── application.properties
└── pom.xml
```

## Configuration

Key configuration options in `application.properties`:
- `jwt.secret`: Secret key for JWT signing (change in production!)
- `jwt.expiration`: Token expiration time in milliseconds (default: 86400000 = 24 hours)
- Database connection settings

## Security Best Practices Implemented

1. ✅ Parameterized queries (SQL injection protection)
2. ✅ Input sanitization (XSS protection)
3. ✅ Password hashing with BCrypt
4. ✅ JWT token-based authentication
5. ✅ Protected endpoints require authentication
6. ✅ Stateless session management

