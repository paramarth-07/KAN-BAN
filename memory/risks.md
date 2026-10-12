# Risk Analysis

- **CSRF on AJAX Status Changes**: Drag-and-drop AJAX calls must include Spring Security CSRF header (`X-CSRF-TOKEN`). Mitigated by including meta tags in `layout.html` and extracting them in `kanban.js`.
- **Cross-User Data Leakage**: Users attempting to access another user's board by guessing `/boards/{id}`. Mitigated by explicit owner verification in `BoardService` and throwing `AccessDeniedException` / `ResourceNotFoundException`.
- **CSS Viewport Overflow on Mobile**: Fixed column widths could overflow 390px screens. Mitigated by using CSS grid / flex with wrap, `max-width: 100%`, and media queries.
