# 🚀 KanbanPop! &mdash; Full-Stack Enterprise Kanban Management System

A collaborative, multi-board Trello-style task management application built with **Spring Boot 3**, **Spring Security 6**, **Spring Data JPA**, **Thymeleaf**, **Hibernate Validator**, and the **Playful Geometric Design System** (Memphis pop aesthetics, tactile candy buttons, and hard offset shadows).

---

## ✨ Features

- **Multi-Board Workspaces**: Create, edit, and manage multiple independent boards with relational JPA mapping.
- **Interactive 3-Column Kanban Board**:
  - Columns: **To Do**, **In Progress**, and **Done** with dynamic counters.
  - Smooth **HTML5 Drag-and-Drop** task cards with optimistic UI updates.
  - Fallback accessible stepper buttons (`Next →` / `← Reopen`) on every card.
  - **Full-Window Confetti Celebration**: Dynamic canvas confetti animation bursts across the screen whenever a task is moved to **Done**.
- **Role-Based Access Control (RBAC)**:
  - **Standard Users (`ROLE_USER`)**: Private workspaces; isolated from other users' boards.
  - **Administrators (`ROLE_ADMIN`)**: Supervisory portal (`/admin`) to manage all users, toggle admin privileges, and supervise system-wide boards.
  - Passwords hashed with **BCrypt**.
  - All form submissions protected with Spring Security **CSRF** tokens.
- **Strict Input Validation**:
  - Declarative validation via **Hibernate Validator** (`@NotBlank`, `@Size`, `@FutureOrPresent`, `@Email`).
  - Clear, user-friendly field-level error messages.
- **Dual Database Profiles**:
  - **H2 In-Memory Database** enabled by default for zero-friction local execution (zero database installation required).
  - Pre-configured profile for **MySQL** / **PostgreSQL** in `application.yml`.

---

## 🔑 Pre-Configured Test Accounts

The application automatically seeds the database on startup with two accounts:

| Role | Username | Password | Access Scope |
| :--- | :--- | :--- | :--- |
| **Demo User** | `demo` | `password123` | Personal boards ("Product Sprint & Launch", "Growth Campaign") |
| **Admin** | `admin` | `admin123` | Supervisory `/admin` dashboard & all system boards |

*New accounts can also be registered at `/register` and will automatically be assigned `ROLE_USER`.*

---

## 🚀 How to Run

### Prerequisites
- **Java 17 or higher** (Java 21 recommended)
- That's it! The Maven wrapper (`./mvnw`) is included, so Maven does **not** need to be pre-installed.

### Start the Application
Open a terminal in this project directory and run:

**macOS / Linux:**
```bash
./mvnw spring-boot:run
```

**Windows (Command Prompt / PowerShell):**
```cmd
mvnw.cmd spring-boot:run
```

Once started, open your browser and navigate to:
👉 **`http://localhost:8080`**

### Running Tests
To run the automated integration test suite:
```bash
./mvnw test
```

### H2 Database Console
To inspect the in-memory database directly:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:kanbandb`
- User: `sa`
- Password: *(leave blank)*

---

## 📂 Project Structure

```
kanban-system/
├── pom.xml
├── mvnw / mvnw.cmd
├── src/main/java/com/example/kanban/
│   ├── KanbanApplication.java
│   ├── controller/      # Web & REST controllers (Board, Task, Auth, Admin)
│   ├── service/         # Transactional business logic with RBAC checks
│   ├── repository/      # Spring Data JPA repositories
│   ├── model/           # User, Board, Task JPA entities & Enums
│   ├── dto/             # Validation form DTOs
│   ├── security/        # Spring Security 6 config & UserDetailsService
│   └── exception/       # @ControllerAdvice global error handler
└── src/main/resources/
    ├── application.yml  # Application settings (H2 & MySQL profiles)
    ├── static/          # Playful Geometric CSS & Kanban interactive JS
    └── templates/       # Thymeleaf HTML views
```
