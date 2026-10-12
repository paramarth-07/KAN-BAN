# Project Constitution: Kanban Task Management System

## 1. Architectural Invariants & Rules
- Standard Enterprise Spring Boot 3 Layered Architecture: `controller`, `service`, `repository`, `model`, `dto`, `security`, `exception`.
- Server-side rendering with Thymeleaf 3 + Spring Security 6 Extras.
- Playful Geometric Design System:
  - Palette: `#FFFDF5` (Warm Cream), `#1E293B` (Slate 800), `#8B5CF6` (Vivid Violet), `#F472B6` (Hot Pink), `#FBBF24` (Amber), `#34D399` (Mint).
  - Borders: 2px solid `#1E293B`.
  - Shadows: Hard offset shadows (4px 4px 0px `#1E293B`).
  - Typography: Headings ("Outfit", 700/800), Body ("Plus Jakarta Sans", 400/500/600).
- Strict RBAC: Unauthenticated can only access `/login`, `/register`, and static assets. ROLE_USER manages own boards/tasks. ROLE_ADMIN has access to `/admin` dashboard.
- Zero fixed CSS widths > 390px; full 390px mobile responsiveness.

## 2. Data Schemas
### Input Schema
```json
{
  "user_registration": {
    "username": "string (3-50 chars, required)",
    "email": "string (valid email format, required)",
    "password": "string (6-100 chars, required)"
  },
  "board_input": {
    "title": "string (1-100 chars, required)",
    "description": "string (optional, text)"
  },
  "task_input": {
    "title": "string (1-100 chars, required)",
    "description": "string (optional, text)",
    "status": "TODO | IN_PROGRESS | DONE",
    "priority": "LOW | MEDIUM | HIGH | URGENT",
    "dueDate": "YYYY-MM-DD (optional, present or future)"
  }
}
```

### Processing Schema
```json
{
  "user_entity": {
    "id": "Long (Primary Key)",
    "username": "String (Unique)",
    "email": "String (Unique)",
    "password": "String (BCrypt hash)",
    "role": "Role (ROLE_USER | ROLE_ADMIN)",
    "boards": "List<Board> (Cascade.ALL, orphanRemoval=true)"
  },
  "board_entity": {
    "id": "Long (Primary Key)",
    "title": "String",
    "description": "String",
    "createdAt": "LocalDateTime",
    "user": "User (Owner, ManyToOne LAZY)",
    "tasks": "List<Task> (Cascade.ALL, orphanRemoval=true)"
  },
  "task_entity": {
    "id": "Long (Primary Key)",
    "title": "String",
    "description": "String",
    "status": "TaskStatus (TODO, IN_PROGRESS, DONE)",
    "priority": "TaskPriority (LOW, MEDIUM, HIGH, URGENT)",
    "dueDate": "LocalDate",
    "board": "Board (ManyToOne LAZY)"
  }
}
```

### Output Schema
```json
{
  "board_view": {
    "boardId": "Long",
    "title": "String",
    "description": "String",
    "ownerUsername": "String",
    "todoTasks": "List<Task>",
    "inProgressTasks": "List<Task>",
    "doneTasks": "List<Task>"
  },
  "api_task_status_response": {
    "success": true,
    "taskId": 1,
    "newStatus": "IN_PROGRESS",
    "message": "Task status updated successfully"
  }
}
```

## 3. Security Constraints & Boundaries
- Passwords must be encoded using BCrypt with minimum strength 10.
- All non-GET requests protected by Spring Security CSRF tokens.
- Cross-user authorization checks enforced in service layer (users cannot view/edit/delete another user's board or task unless admin).
- SQL injection prevented via Spring Data JPA parameterized queries.
- XSS prevented via Thymeleaf expression escaping (`th:text`).

## 4. Coding Standards & Naming Conventions
- Java packages under `com.example.kanban`.
- Classes: PascalCase (e.g. `BoardController`, `TaskService`).
- Methods & variables: camelCase.
- HTML templates in `src/main/resources/templates/`.
- CSS & JS in `src/main/resources/static/css/` and `src/main/resources/static/js/`.
