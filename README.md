# Spring Security with JWT — Complete Guide

A Spring Boot REST API secured with JWT authentication, role-based access control, and PostgreSQL.

---

## Tech Stack

- Java 17
- Spring Boot 3.2.3
- Spring Security
- JWT (jjwt 0.12.3)
- Spring Data JPA
- PostgreSQL
- Lombok

---

## Project Structure

```
src/main/java/com/example/springsecurity/
│
├── config/
│   ├── SecurityConfig.java          # Security rules, filter chain, beans
│   ├── JwtAuthFilter.java           # Runs on every request, validates JWT
│   └── GlobalExceptionHandler.java  # Handles 401, 403, validation errors
│
├── controller/
│   ├── AuthController.java          # /api/auth/register and /api/auth/login
│   └── TestController.java          # Protected endpoints for testing roles
│
├── dto/
│   ├── RegisterRequest.java         # Input shape for registration
│   ├── LoginRequest.java            # Input shape for login
│   └── AuthResponse.java            # Output shape — token + email + role
│
├── entity/
│   ├── User.java                    # User entity + implements UserDetails
│   └── Role.java                    # Enum — USER, ADMIN
│
├── repository/
│   └── UserRepository.java          # findByEmail, existsByEmail
│
└── service/
    ├── AuthService.java             # Register and login business logic
    ├── JwtService.java              # Generate, extract, validate JWT
    └── UserDetailsServiceImpl.java  # Loads user from DB for Spring Security
```

---

## Setup

### 1. Create PostgreSQL database

```sql
CREATE DATABASE springsecurity_db;
```

### 2. Update `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/springsecurity_db
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

app.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
app.jwt.expiration=86400000
```

### 3. Run the app

```bash
mvn spring-boot:run
```

---

## API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | None | Register a new user |
| POST | `/api/auth/login` | None | Login and get JWT token |
| GET | `/api/user/hello` | Bearer Token | Accessible by USER and ADMIN |
| GET | `/api/admin/hello` | Bearer Token (ADMIN) | Accessible by ADMIN only |
| GET | `/api/profile` | Bearer Token | Shows logged in user info |

---

## Testing with Postman

### Register a USER
```json
POST /api/auth/register

{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@gmail.com",
    "password": "123456",
    "role": "USER"
}
```

### Register an ADMIN
```json
POST /api/auth/register

{
    "firstName": "Super",
    "lastName": "Admin",
    "email": "admin@gmail.com",
    "password": "123456",
    "role": "ADMIN"
}
```

### Login
```json
POST /api/auth/login

{
    "email": "john@gmail.com",
    "password": "123456"
}
```

Response:
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "john@gmail.com",
    "role": "USER"
}
```

### Hit a protected endpoint
```
GET /api/user/hello
Authorization: Bearer <your_token_here>
```

---

## How It Works

```
REGISTER / LOGIN:
Request → SecurityConfig (permitAll) → AuthController → AuthService → JwtService → Token returned

PROTECTED REQUEST:
Request → JwtAuthFilter (validates token) → SecurityConfig (checks role) → Controller → Response
```

### JwtAuthFilter — runs on every request
1. Checks for `Authorization: Bearer <token>` header
2. Extracts email from token
3. Loads user from database
4. Validates token (email match + not expired)
5. Sets user in `SecurityContextHolder`
6. Passes request to controller

### SecurityConfig — the rulebook
- `/api/auth/**` → public, no token needed
- `/api/admin/**` → ADMIN role only
- `/api/user/**` → USER or ADMIN
- Sessions are STATELESS — no server-side sessions, JWT handles everything

---

## File Purpose — One Line Each

| File | Job |
|------|-----|
| `Role.java` | Defines USER and ADMIN |
| `User.java` | User entity + Spring Security bridge via UserDetails |
| `UserRepository.java` | DB queries — findByEmail, existsByEmail |
| `RegisterRequest.java` | Input DTO for registration |
| `LoginRequest.java` | Input DTO for login |
| `AuthResponse.java` | Output DTO — token + email + role |
| `JwtService.java` | Create, read, validate JWT tokens |
| `UserDetailsServiceImpl.java` | Load user from DB for Spring Security |
| `JwtAuthFilter.java` | Intercepts every request and checks token |
| `SecurityConfig.java` | All security rules in one place |
| `AuthService.java` | Register and login business logic |
| `AuthController.java` | Public endpoints — register and login |
| `TestController.java` | Protected test endpoints |

---

## Key Concepts

- **JWT is stateless** — no session stored on server, token itself proves identity
- **BCrypt** — one-way password hashing, never store plain text passwords
- **SecurityContextHolder** — holds who is logged in for that request's lifetime
- **UserDetails** — interface that connects your User class to Spring Security
- **STATELESS** — every request must carry its own JWT token
