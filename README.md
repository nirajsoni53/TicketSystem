# Support Ticketing System Backend

This project implements a simplified support ticketing system backend using Spring Boot, focusing on **JWT (JSON Web Token)** based authentication and ownership-based access control.

## Requirements Implemented

* **User Authentication**: Users log in to receive a JWT token.

* **JWT Token Validation**: All protected endpoints require a valid JWT token.

* **Ticket Creation**: Authenticated users can create support tickets.

* **Access Control (List Tickets)**:

    * **USER Role**: Can only view tickets they have created.

    * **AGENT Role**: Can only view tickets assigned to them.

* **Mock Data**: In-memory repositories are used for users and tickets.

* **Global Exception Handling**: Provides structured JSON error responses (401, 403, 400/404, 500).

## Setup and Run Instructions

1.  **Prerequisites**:

    * Java 17 or higher

    * Maven 3.6+

2.  **Clone the Repository**:

    ```bash
    git clone https://github.com/nirajsoni53/TicketSystem.git
    cd ticket-system
    ```

3.  **Build the Project**:

    ```bash
    mvn clean install
    ```

4.  **Run the Application**:

    ```bash
    mvn spring-boot:run
    ```

    The application will start on `http://localhost:8080`.

## Mock User Setup and Credentials

The `UserRepository` (`src/main/java/com/example/ticketsystem/repository/UserRepository.java`) is initialized with:

| Username | Password | Role | User ID |
| :------- | :------- | :--- | :------ |
| `user1` | `pass123` | `USER` | `user1-id` |
| `john` | `pass123` | `USER` | `user2-id` |
| `agent1` | `agentpass` | `AGENT` | `agent1-id` |
| `agent2` | `agentpass` | `AGENT` | `agent2-id` |

## How JWT is Used and Verified

* **Key Generation**: A symmetric HMAC SHA-256 key (256-bit) is used for JWT signing. It's base64 encoded in `application.properties`.

* **Token Issuance (`POST /auth/login`)**:

    * Valid `username`/`password` are authenticated by Spring Security.

    * `JwtTokenProvider` generates a JWT with `userId` (`sub`), `role` (custom claim), `iat`, and `exp` (1-hour validity).

    * The token is signed with HS256 and returned.

* **Token Verification**:

    * `JwtAuthenticationFilter` extracts JWT from `Authorization: Bearer <token>` header.

    * `JwtTokenProvider` validates token signature and expiration.

    * If valid, claims are extracted. The `role` (e.g., "USER") is prefixed to "ROLE\_USER" for Spring Security.

    * An `Authentication` object (with `userId` as principal and prefixed roles) is set in `SecurityContextHolder`.

* **Error Handling for Token Issues**:

    * `JwtException` (e.g., invalid/expired token) from the filter is caught by Spring Security's `ExceptionTranslationFilter`.

    * The `authenticationEntryPoint` in `SecurityConfig` handles this, sending a structured JSON `401 Unauthorized` response.

## API Specification and Usage Examples

Use Postman or `curl` to test.

### 1. Login

* **Endpoint**: `POST http://localhost:8080/auth/login`

* **Payload**: `{"username": "user1", "password": "pass123"}` or `{"username": "agent1", "password": "agentpass"}`

* **Response**: `{"token": "<JWT token>"}` (Copy this token)

### 2. Create Ticket

* **Endpoint**: `POST http://localhost:8080/tickets`

* **Headers**: `Authorization: Bearer <YOUR_JWT_TOKEN>`, `Content-Type: application/json`

* **Payload**: `{"subject": "Payment issue", "description": "Was charged twice."}`

* **Response**: `HTTP 201 Created` with created ticket details.

### 3. List Tickets

* **Endpoint**: `GET http://localhost:8080/tickets`

* **Headers**: `Authorization: Bearer <YOUR_JWT_TOKEN>`

* **Response**: `HTTP 200 OK`

    * **For `USER`**: Returns tickets created by them.

    * **For `AGENT`**: Returns tickets assigned to them.

## AI Tool Usage and Validation Steps

This project was developed with a combination of direct implementation and strategic assistance from a AI.

* **AI Assistance Utilized**: This project was developed primarily through direct coding and engineering. I used a AI as a helpful assistant for generating initial boilerplate code and setting up basic configurations.

* **Validation**: I performed thorough manual testing of all API endpoints using Postman, including detailed role-based access checks, to ensure everything worked correctly across various scenarios.
