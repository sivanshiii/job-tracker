# Job Tracker

A full-stack web application for managing job applications, tracking progress through the recruitment pipeline, and keeping your job search organized.

**React · Spring Boot · PostgreSQL · JWT · Docker**

---

## Overview

Job Tracker provides a centralized place to manage job applications, track their status, and monitor progress throughout the job search process.

## Features

- **User Authentication** — Register and log in with JWT-based authentication.
- **Application Management** — Create, view, update, and delete job applications.
- **Application Pipeline** — Track applications across Wishlist, Applied, Interview, Offer, and Rejected stages.
- **Status Updates** — Update an application's recruitment stage.
- **Search & Filtering** — Search applications and filter by status.
- **Pagination** — Navigate through application records.
- **Dashboard Statistics** — View application counts across different statuses.
- **Data Isolation** — Users can access only their own application records.

## Tech Stack

| Layer | Technologies |
|---|---|
| Frontend | React 18, React Router, Vite, CSS |
| Backend | Java 17, Spring Boot 3.3, Spring Web |
| Security | Spring Security, JWT, BCrypt |
| Database | PostgreSQL 16, Spring Data JPA, Hibernate |
| API Documentation | Swagger / OpenAPI |
| Testing | JUnit 5, Mockito |
| Containerization | Docker, Docker Compose |

## Architecture

```text
React Frontend
      |
      | REST API + JWT
      v
Spring Boot Backend
      |
      | Spring Data JPA / Hibernate
      v
PostgreSQL Database
```

The backend follows a layered architecture:

```text
Controller → Service → Repository → Database
```

## Application Statuses

| Status | Description |
|---|---|
| Wishlist | Potential opportunities to apply for |
| Applied | Applications submitted |
| Interview | Applications progressing through interviews |
| Offer | Offers received |
| Rejected | Applications that were unsuccessful |

## Project Structure

```text
job-tracker/
├── backend/
│   ├── src/
│   │   ├── main/java/com/jobtracker/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── App.jsx
│   │   ├── AuthContext.jsx
│   │   └── api.js
│   └── package.json
├── docker-compose.yml
└── README.md
```

## Getting Started

### Prerequisites

- Java 17
- Maven
- Node.js 18+
- Docker Desktop

### 1. Clone the repository

```bash
git clone https://github.com/sivanshiii/job-tracker.git
cd job-tracker
```

### 2. Start PostgreSQL

```bash
docker compose up -d db
```

### 3. Start the Backend

Open a terminal:

```bash
cd backend
mvn spring-boot:run
```

The backend runs at:

`http://localhost:8080`

### 4. Start the Frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

The frontend runs at:

`http://localhost:5173`

### 5. Access API Documentation

Swagger UI:

`http://localhost:8080/swagger-ui.html`

Hibernate automatically manages the database schema using the configured JPA settings.

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Authenticate a user |

### Job Applications

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/jobs` | List applications with search, filtering and pagination |
| GET | `/api/jobs/stats` | Retrieve application statistics |
| GET | `/api/jobs/{id}` | Retrieve an application |
| POST | `/api/jobs` | Create an application |
| PUT | `/api/jobs/{id}` | Update an application |
| PATCH | `/api/jobs/{id}/status` | Update application status |
| DELETE | `/api/jobs/{id}` | Delete an application |

Protected endpoints require a JWT:

```http
Authorization: Bearer <token>
```

## Security

- JWT-based authentication.
- Password hashing using BCrypt.
- Protected REST API endpoints.
- User-specific application access.
- Request validation.
- Centralized exception handling.

## Testing

Backend unit tests are written using JUnit 5 and Mockito.

Run the tests:

```bash
cd backend
mvn test
```

## Future Improvements

- Visual analytics and application trends.
- Interview round tracking.
- Follow-up reminders.
- CSV export.
- Integration testing with Testcontainers.

---

Built as a full-stack project to explore REST API development, authentication, database design, and frontend-backend integration using Java and React.
