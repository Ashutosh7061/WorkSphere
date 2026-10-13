# WorkSphere

WorkSphere is a Spring Boot REST API for managing workplace infrastructure and employee operations. It provides APIs for companies, campuses, buildings, floors, rooms, desks, employees, and auditoriums.

## Features

- JWT-based authentication with stateless Spring Security sessions
- Role-based access control for protected management operations
- Company, campus, building, floor, room, and desk management
- Auditorium management and status updates
- Employee profile management
- MySQL persistence through Spring Data JPA
- Request validation and centralized exception handling
- Automatic creation of an initial `SUPER_ADMIN` account on first startup

## Technology stack

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Security
- Spring Data JPA and Hibernate
- MySQL Connector/J
- JSON Web Token (JJWT) 0.12.6
- Maven Wrapper

## Prerequisites

- JDK 21
- MySQL 8 or a compatible MySQL server
- A database named `worksphere`

## Configuration

The application reads its configuration from:

```text
src/main/resources/application.properties
```

Configure the database connection and JWT settings before starting the application:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/worksphere
spring.datasource.username=YOUR_DATABASE_USER
spring.datasource.password=YOUR_DATABASE_PASSWORD

jwt.secret=YOUR_LONG_RANDOM_SECRET_KEY
jwt.expiration=3600000
```

The API uses the following context path:

```text
/api/v1
```

Do not commit real database passwords or JWT secrets. Use environment-specific configuration or a secrets manager for shared and production environments. The `application.properties.demo` file can be used as a configuration template.

## Run locally

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

The application starts on the default Spring Boot port, `8080`. For example, the login endpoint is available at:

```text
POST http://localhost:8080/api/v1/auth/login
```

## Build and test

Build the application:

```powershell
.\mvnw.cmd clean package
```

Run the test suite:

```powershell
.\mvnw.cmd test
```

The Maven Wrapper can also be invoked as `./mvnw` on macOS and Linux.

## API overview

All endpoints are prefixed with `/api/v1`. Except for login, endpoints require a valid JWT bearer token:

```http
Authorization: Bearer <token>
```

| Area | Main routes |
| --- | --- |
| Authentication | `/auth/login`, `/auth/me` |
| Companies | `/companies` |
| Campuses | `/campuses` |
| Buildings | `/campuses/{campusCode}/buildings` |
| Floors | `/campuses/{campusCode}/buildings/{buildingCode}/floors` |
| Rooms | `/rooms` |
| Desks | `/desks` |
| Auditoriums | `/auditoriums` |
| Employees | `/employees/profile` |
| Cross-resource lookups | `/resources/buildings`, `/resources/floors`, `/resources/campuses/{campusCode}/floors` |

Most resource-management operations are currently restricted to the `SUPER_ADMIN` role. Refer to the controllers under `src/main/java/com/ashutosh/WorkSphere/controller` for the complete request and response contract.

### Login example

Request:

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "your-email@example.com",
  "password": "your-password"
}
```

The response contains a JWT token. Send that token in the `Authorization` header when calling protected endpoints.

## Project layout

```text
src/main/java/com/ashutosh/WorkSphere/
├── config/       Application and security configuration
├── controller/   REST endpoints
├── dto/          Request and response models
├── entity/       JPA entities
├── enums/        Domain enumerations
├── exception/    Domain exceptions and global error handling
├── repository/   Spring Data repositories
├── security/     JWT authentication components
└── service/      Business logic
```

## Initial administrator

On the first startup, `DataInitializer` creates a `SUPER_ADMIN` account when one does not already exist. Change the initial credentials and remove or replace the bootstrap behavior before deploying to a shared or production environment.

## License

No license has been specified yet.
