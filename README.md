# 1. Project overview

**Academix** is a Spring Boot application that implements an online learning / course platform with the following capabilities (implemented or scaffolded in code found in this repository):

* Multi-role user model (Admin, Teacher, Student).
* Course management (courses, lectures, documents, content).
* Exams, questions, attempts, and progress tracking.
* Enrollment and payments (Stripe integration, Stripe Connect for teacher onboarding).
* File storage abstraction backed by Supabase (signed upload / signed download flows).
* Certificate issuance and a vault/withdrawal subsystem for teacher payouts.
* OAuth2 (GitHub) login plus JWT for API authentication and a Thymeleaf-based minimal UI (login, register, enroll pages).

---

# 2. Tech stack

* Java (Spring Boot 3.x)
* Spring Data JPA (Hibernate)
* Spring Security + OAuth2 client
* JWT (io.jsonwebtoken / JJWT)
* MySQL (jdbc:mysql)
* Stripe Java SDK (server-side payments & webhooks)
* Supabase Storage (signed upload/download)
* Thymeleaf (minimal server-side pages)
* Lombok, MapStruct / ModelMapper
* Maven build

> Spring Boot parent in `pom.xml` is `3.5.3` — Java 17+ is recommended.

---

# 3. Project layout (important packages)

* `com.talha.academix` — main application class (`AcademixApplication`)
* `config` — Spring Security configuration and application configuration
* `controllers` — REST controllers & view controllers (API surface)
* `services` — service layer implementations
* `repository` — Spring Data JPA repositories
* `model` — JPA entities
* `dto` — request/response DTOs
* `security` — custom `UserDetails`, OAuth2 success handler, etc.
* `util` — JWT utilities and auth filter
* `resources/templates` — Thymeleaf pages (`login.html`, `enroll.html`, etc.)
* `resources/application.properties` — default config used by the application

---

# 4. Key database entities

The repository contains ~25 JPA entities. Primary ones (file names) and purpose:

* `User` — app users (Admin / Teacher / Student)
* `Course` — course entity
* `Lecture` — lecture inside course
* `Content` — course content (videos, text)
* `Document` — attached files / documents
* `Enrollment` — course enrollment record
* `Exam`, `Question`, `QuestionOption` — exam and question models
* `Attempt`, `AttemptAnswer` — exam attempt tracking
* `Payment`, `StripePaymentDetail`, `StripePaymentEvent`, `StripeWebhookEvent` — payment & event tracking
* `StoredFile` — metadata for files stored in Supabase
* `Certificate` — course certificate records
* `TeacherAccount`, `TeacherQualification` — teacher profile/account/onboarding
* `Vault`, `VaultTransaction`, `Withdrawal` — teacher/withdrawal bookkeeping
* `LectureProgress`, `DocumentProgress`, `StudentContentProgress` — progress tracking

---

# 5. Important API endpoints (summary)

Controller base paths:

* `GET /` — redirects to `/login` (Thymeleaf view)
* `GET /login`, `GET /register` — views
* `GET /oauth2/callback` — OAuth2 callback view

REST controllers (base paths):

* `/api/users` — user registration, listing, update, deletion (JWT authentication protected)

  * Public routes (whitelisted in JWT filter): `/api/users/register`, `/api/users/login`, `/api/users/auth`, `/api/users/welcome` (see security filter)
* `/api/courses` — create/list/update/delete courses
* `/api/lectures` — lecture CRUD and playback/progress
* `/api/contents` — content CRUD
* `/api/documents` — document management
* `/api/enrollments` — enroll/unenroll & enrollment listing
* `/api/exams`, `/api/questions`, `/api/options`, `/api/attempts`, `/api/attempt-answers` — exam lifecycle and answers
* `/api/payments` — initiate payments, fetch publishable key
* `/api/stripe/connect` — teacher Stripe Connect onboarding (onboarding link)
* `/stripe/webhook` — Stripe webhook endpoint (must configure `stripe.webhook-secret`)
* `/api/files` — Supabase signed upload / mark-ready / signed-download
* `/api/certificates` — certificate generation & download
* `/api/vaults`, `/api/withdrawals` — payout & withdrawal flows
* `/api/admin/dashboard` — admin dashboard endpoints
* `ViewController` serves the minimal UI views

---

# 6. Security & auth

* JWT-based authentication is implemented (`JwtService`, `JwtAuthFilter`).

  * `JwtAuthFilter` skips auth for public pages and the endpoints listed in section 5.
* OAuth2 GitHub login is present with an `OAuth2AuthenticationSuccessHandler` that:

  * creates a local `User` (default role `STUDENT`) for OAuth users if not present,
  * issues a JWT and redirects/carries a token in a redirect (see handler code).
* `application.properties` contains a default user (`spring.security.user.name=talha`, `spring.security.user.password=2612`) for simple form login and initial access.

* Many credentials in `application.properties` are placeholders or stored directly — do not use this in production.

Security recommendation (must do before production):

* Move all secrets (DB password, JWT secret, Stripe keys, Supabase keys, OAuth client secret) into environment variables or a secret manager.

---

# 7. Prerequisites (development)

* Java 17+ (required by Spring Boot 3.x)
* Maven 3.6+
* MySQL server (create database `AcademixDB` or change URL)
* Stripe account & test keys (for payments, and Stripe Connect for teacher onboarding)
* Supabase project (for file storage) or modify file storage implementation
* Optional: GitHub OAuth app for OAuth login

---

# 8. Build & run (development)

From project root (where `pom.xml` is):

```bash
# Build
mvn clean package

# Run (from target)
java -jar target/academix-0.0.1-SNAPSHOT.jar

# Alternatively run with Maven (dev)
mvn spring-boot:run
```

The app default port is **8081** (see `application.properties`). Access `http://localhost:8081/login`.

To run with a different `application.properties`, use Spring profiles or pass overrides:

---

# 9. Stripe & webhooks

* Stripe is used for payments and teacher payouts (`stripe-java` SDK).
* Configure `STRIPE_SECRET_KEY` and `stripe.publishable-key` in env or `application.properties`.
* Stripe webhook endpoint: `/stripe/webhook`. You must configure the Stripe dashboard webhook with the endpoint URL and set `stripe.webhook-secret` in config.
* For local webhook testing, use `stripe listen` and forward to `http://localhost:8081/stripe/webhook` (in Stripe CLI).

Teacher Stripe Connect:

* `TeacherStripeConnectController` provides an onboarding link route that uses `TeacherAccountServiceImpl`. Ensure your server URLs (refresh, return) are accurate in production.

---

# 10. File storage (Supabase)

* The app uses Supabase storage for media and document hosting.
* Flow implemented in `StoredFileController`:

  1. `POST /api/files/initiate-signed-upload` — server returns signed upload parameters (Supabase).
  2. Client uploads directly to Supabase using signed URL.
  3. `POST /api/files/{id}/mark-ready` — notify server that upload completed.
  4. `GET /api/files/{id}/signed-download` — obtain signed download URL for playback or download.

Configure:

* `supabase.url`, `supabase.apiKey` (public), `supabase.serviceRoleKey` (server-side privileged key), and `supabase.storage.bucket`.

---

# 11. Quick start checklist (short)

1. Install Java 17+, Maven, MySQL, StripeCLI.
2. Create DB: `CREATE DATABASE AcademixDB;`
3. Set env vars or edit `application.properties`:

   * `spring.datasource.*`, `STRIPE_SECRET_KEY`, `stripe.publishable-key`, `stripe.webhook-secret`, `supabase.*`, `spring.security.oauth2.client.registration.github.client-id/secret`, etc.
4. `mvn clean package`
5. `java -jar target/academix-0.0.1-SNAPSHOT.jar`
6. Browse `http://localhost:8081/login`

---