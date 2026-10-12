# WorkSphere

WorkSphere is a workplace management application for organizing office resources and employee operations.
It helps manage campuses, buildings, floors, rooms, desks, employees, and auditorium bookings efficiently.
Built with Java 21 and Spring Boot, the project provides secure authentication and REST-based backend services.

## Features
- Campus, building, floor, room, desk, and employee management
- Auditorium status and booking workflows
- JWT-based authentication and role-based security
- MySQL-backed persistence with Spring Data JPA

## Tech Stack
- Java 21
- Spring Boot 4.1.1
- Spring Security
- Spring Data JPA
- MySQL

## Run the project
```bash
./mvnw spring-boot:run
```

## Configuration
Update database settings in `src/main/resources/application.properties` before running the application.
