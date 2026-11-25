# Job Tracker (Spring Boot + React + PostgreSQL)

A full stack app to log job applications, move each one through a pipeline
(Wishlist, Applied, Interview, Offer, Rejected), search, filter and paginate.

## Tech stack

| Layer | Tools |
|---|---|
| Backend | Java 17, Spring Boot 3.3, Spring Web, Spring Data JPA (Hibernate), Spring Security, JWT (jjwt), Bean Validation |
| Database | PostgreSQL 16 |
| Frontend | React 18, React Router, Vite, plain CSS |
| Quality | JUnit 5 + Mockito, Swagger/OpenAPI docs |
| DevOps | Docker, Docker Compose, AWS EC2 |

## Project structure

```
job-tracker/
  backend/   Spring Boot API (controller -> service -> repository -> entity)
  frontend/  React app
  docker-compose.yml
```

## Run it locally

You need: JDK 17, Maven, Node 18+, and Docker (for PostgreSQL).

```bash
# 1. Start PostgreSQL only
docker compose up -d db

# 2. Start the API (http://localhost:8080)
cd backend
mvn spring-boot:run

# 3. Start the React app (http://localhost:5173)
cd ../frontend
npm install
npm run dev
```

Swagger UI: http://localhost:8080/swagger-ui.html
Tables are created automatically by Hibernate (`ddl-auto=update`).

To run API + database fully in Docker: `docker compose up --build`

## API

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Create an account, returns a JWT |
| POST | `/api/auth/login` | Log in, returns a JWT |
| GET | `/api/jobs?status=&q=&page=&size=` | List your applications (filter, search, paginate) |
| GET | `/api/jobs/stats` | Count of applications per status |
| GET | `/api/jobs/{id}` | One application |
| POST | `/api/jobs` | Create |
| PUT | `/api/jobs/{id}` | Edit |
| PATCH | `/api/jobs/{id}/status` | Change only the status |
| DELETE | `/api/jobs/{id}` | Delete |

Everything under `/api/jobs` needs the header `Authorization: Bearer <token>`.
Each user only ever sees their own rows (queries filter by the logged-in email).

Quick test with curl:

```bash
curl -X POST localhost:8080/api/auth/register -H "Content-Type: application/json" \
  -d '{"fullName":"Test User","email":"test@example.com","password":"password123"}'

curl localhost:8080/api/jobs -H "Authorization: Bearer <paste token>"
```

## Build plan (about 5 sessions)

Build it yourself in this order instead of pasting everything at once. You will
need to explain every line in an interview, so type it, run it, and break it on purpose.

**Session 1: Backend skeleton and database**
- Create the project, run `docker compose up -d db`, start the app.
- Read and type: `User`, `JobApplication`, `Status`, the two repositories.
- Check in `psql` or DBeaver that the tables appeared. Learn what `@Entity`, `@ManyToOne`, `@Enumerated` do.

**Session 2: Authentication**
- `RegisterRequest`, `LoginRequest`, `AuthService`, `AuthController`.
- Then `JwtService`, `JwtAuthFilter`, `SecurityConfig`.
- Test register and login in Swagger. Try a protected URL without a token (expect 401).
- Learn: BCrypt hashing, what a JWT contains, why the filter runs before the controller.

**Session 3: Job CRUD**
- `JobRequest`, `JobResponse`, `PageResponse`, `JobService`, `JobController`, `GlobalExceptionHandler`.
- Test every endpoint in Swagger. Register two users and confirm user A cannot read user B's job (expect 404).
- Learn: DTO vs entity, validation errors (400), pagination, `@Transactional`.

**Session 4: React frontend**
- `api.js`, `AuthContext.jsx`, `AuthPage.jsx`, then `Dashboard.jsx` and `JobForm.jsx`.
- Learn: how the JWT is stored and attached to requests, protected routes, `useEffect` for loading data.

**Session 5: Tests, Docker, deploy**
- Run `mvn test`. Add 2 more tests of your own (for example: updating someone else's job fails).
- `docker compose up --build`, then deploy to AWS (below).
- Write screenshots and a features list into this README.

## Deploy on AWS (you already know EC2)

1. Launch an EC2 instance (Ubuntu), install Docker, open ports 80 and 22 in the Security Group (not 5432 or 8080 to the world).
2. Copy the project over (git clone) and run the API + Postgres with `docker compose up -d --build`.
   Set a real `JWT_SECRET` and database password in `docker-compose.yml` or an `.env` file.
3. Build the frontend with `VITE_API_URL=http://<your-ip-or-domain>:8080 npm run build` and serve `dist/` with Nginx
   (or put it on S3 + CloudFront), and set `CORS_ORIGINS` on the API to that frontend URL.
4. Optional upgrade: use RDS PostgreSQL instead of the Postgres container, and an ALB in front, as in your malware-analysis project.

## Resume bullets (use only what you actually built)

**Job Application Tracker** | Java, Spring Boot, Spring Security, JPA/Hibernate, PostgreSQL, React, Docker, AWS
- Built a full stack job-tracking app with a Spring Boot REST API (controller-service-repository layers) and a React frontend
- Implemented JWT authentication with Spring Security and BCrypt, with per-user data isolation on every endpoint
- Designed a PostgreSQL schema with JPA/Hibernate; added search, status filtering and pagination
- Wrote JUnit/Mockito unit tests, documented the API with Swagger/OpenAPI, containerised with Docker and deployed on AWS EC2

Then add to the skills section: **Spring Boot, Spring Security, REST APIs, JPA/Hibernate, JUnit, Docker**.

## Interview questions this project prepares you for

- How does JWT authentication work here? Where is the token stored and what are the trade-offs? (localStorage is simple but exposed to XSS; httpOnly cookies are safer.)
- Why DTOs instead of returning entities?
- What does `@Transactional` do? What is `open-in-view=false` for?
- How do you stop one user reading another's data? (`findByIdAndUserEmail`.)
- Lazy vs eager loading, and the N+1 problem.
- How would you add email reminders for follow-ups? (Scheduled job with `@Scheduled`.)

## Ideas to extend it

- Follow-up reminders with `@Scheduled` and email
- Interview rounds as a second entity (one application has many rounds)
- Dashboard charts, CSV export, refresh tokens
- Integration tests with Testcontainers
