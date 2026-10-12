# Deep Analysis: Kanban System Architecture

## Problem Decomposition
The requirement is a monolithic full-stack enterprise web application built on Spring Boot 3.x functioning as a multi-board Trello-style task management system with:
1. Multi-board ownership per user, role-based access control (ROLE_USER, ROLE_ADMIN).
2. 3-column Kanban layout (TODO, IN_PROGRESS, DONE) with priority flags, due dates, modals, and dynamic task status updates.
3. Server-side rendering with Thymeleaf 3, with client-side drag-and-drop interactivity that syncs to the server.
4. "Playful Geometric" aesthetic: Memphis-inspired tactile fun, warm cream canvas `#FFFDF5`, dark slate `#1E293B`, vibrant violet `#8B5CF6`, hot pink `#F472B6`, amber `#FBBF24`, emerald `#34D399`, hard 4px drop shadows, 2px borders, Outfit/Plus Jakarta Sans typography.

## Technical Approaches & Trade-offs
- **H2 vs External DB**: Configured with in-memory / file H2 database enabled by default so the application runs immediately without requiring local MySQL server daemon setup, with easy toggle for MySQL in `application.yml`.
- **Kanban Interactions**: Dual interaction paradigm:
  1. Smooth HTML5 Drag-and-Drop posting asynchronously to `/api/tasks/{id}/status` with CSRF headers.
  2. Direct accessible status step buttons (← Move, Move →) on each card, ensuring full functionality on touch devices and keyboards.
- **Security & Authorization**: Spring Security 6 `SecurityFilterChain` with custom UserDetails; service-level verification ensuring users only access their own boards, while ROLE_ADMIN can supervise all boards.
