package com.example.kanban.config;

import com.example.kanban.model.*;
import com.example.kanban.repository.BoardRepository;
import com.example.kanban.repository.TaskRepository;
import com.example.kanban.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final TaskRepository taskRepository;
    private final com.example.kanban.repository.NoteRepository noteRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           BoardRepository boardRepository,
                           TaskRepository taskRepository,
                           com.example.kanban.repository.NoteRepository noteRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.taskRepository = taskRepository;
        this.noteRepository = noteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Database already seeded. Skipping initial data creation.");
            return;
        }

        logger.info("Seeding initial database data...");

        // 1. Create Admin User
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@kanban.local");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ROLE_ADMIN);
        admin = userRepository.save(admin);

        // 2. Create Demo User
        User demo = new User();
        demo.setUsername("demo");
        demo.setEmail("demo@kanban.local");
        demo.setPassword(passwordEncoder.encode("password123"));
        demo.setRole(Role.ROLE_USER);
        demo = userRepository.save(demo);

        // 3. Demo Board 1: Product Sprint & Launch
        Board sprintBoard = new Board();
        sprintBoard.setTitle("🚀 Product Sprint & Launch");
        sprintBoard.setDescription("Interactive sprint board tracking core MVP deliverables, features, and QA tests.");
        sprintBoard.setUser(demo);
        sprintBoard = boardRepository.save(sprintBoard);

        // Tasks for Sprint Board
        createTask(sprintBoard, "🎨 Playful Geometric Design System",
                "Integrate Memphis-inspired tokens, 4px hard shadows, Outfit typography, and tactile buttons.",
                TaskStatus.IN_PROGRESS, TaskPriority.URGENT, LocalDate.now().plusDays(2), 0);

        createTask(sprintBoard, "🔐 Role-Based Access Control Audit",
                "Verify Spring Security authorization rules for ROLE_USER and ROLE_ADMIN dashboards.",
                TaskStatus.IN_PROGRESS, TaskPriority.HIGH, LocalDate.now().plusDays(3), 1);

        createTask(sprintBoard, "⚡ Async Drag-and-Drop Kanban API",
                "Connect HTML5 drop events to /api/tasks/{id}/status endpoint with CSRF verification.",
                TaskStatus.DONE, TaskPriority.HIGH, LocalDate.now().minusDays(1), 0);

        createTask(sprintBoard, "📦 Spring Data JPA Entity Mapping",
                "Define User, Board, and Task entities with proper cascading and lazy fetch strategies.",
                TaskStatus.DONE, TaskPriority.MEDIUM, LocalDate.now().minusDays(2), 1);

        createTask(sprintBoard, "📱 Mobile Viewport 390px Overflow Test",
                "Ensure zero horizontal scrolling on narrow screens with responsive grid layout.",
                TaskStatus.TODO, TaskPriority.HIGH, LocalDate.now().plusDays(4), 0);

        createTask(sprintBoard, "🧪 End-to-End Playwright Automation",
                "Write automated browser integration tests validating registration, login, and task movement.",
                TaskStatus.TODO, TaskPriority.MEDIUM, LocalDate.now().plusDays(6), 1);

        createTask(sprintBoard, "📄 Export Board Summaries to PDF",
                "Research PDF generation libraries for automated milestone reporting.",
                TaskStatus.TODO, TaskPriority.LOW, LocalDate.now().plusDays(10), 2);

        // 4. Demo Board 2: Marketing & Growth
        Board marketingBoard = new Board();
        marketingBoard.setTitle("📣 Growth & Social Media Campaign");
        marketingBoard.setDescription("Content creation, launch announcements, and community engagement pipeline.");
        marketingBoard.setUser(demo);
        marketingBoard = boardRepository.save(marketingBoard);

        createTask(marketingBoard, "✍️ Write Product Launch Announcement",
                "Draft comprehensive blog post highlighting Trello-style collaboration and Playful Geometric aesthetic.",
                TaskStatus.TODO, TaskPriority.HIGH, LocalDate.now().plusDays(5), 0);

        createTask(marketingBoard, "🎬 Record 60-Second Feature Demo Video",
                "Record high-resolution screen capture showing drag-and-drop board cards and modal editing.",
                TaskStatus.IN_PROGRESS, TaskPriority.URGENT, LocalDate.now().plusDays(1), 0);

        createTask(marketingBoard, "📊 Competitive Feature Matrix Analysis",
                "Benchmark features against Trello, Linear, and Notion Kanban boards.",
                TaskStatus.DONE, TaskPriority.LOW, LocalDate.now().minusDays(3), 0);

        // 5. Admin Board: Enterprise Infrastructure
        Board adminBoard = new Board();
        adminBoard.setTitle("🛡️ Enterprise Operations & Compliance");
        adminBoard.setDescription("Infrastructure security, load balancing, database backups, and telemetry.");
        adminBoard.setUser(admin);
        adminBoard = boardRepository.save(adminBoard);

        createTask(adminBoard, "🔒 Database Backup & Disaster Recovery Drill",
                "Automate nightly encrypted snapshots to cloud storage with retention policy.",
                TaskStatus.TODO, TaskPriority.HIGH, LocalDate.now().plusDays(7), 0);

        createTask(adminBoard, "📈 Telemetry & Health Metrics Dashboard",
                "Integrate Spring Boot Actuator with Prometheus monitoring and Grafana alerts.",
                TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, LocalDate.now().plusDays(4), 0);

        createTask(adminBoard, "🔑 BCrypt Hash Strength Calibration",
                "Benchmark password encoder rounds across target CPU architectures.",
                TaskStatus.DONE, TaskPriority.LOW, LocalDate.now().minusDays(5), 0);

        // Seed Notes for Demo User
        createNote(demo, sprintBoard, "💡 Product Architecture Vision",
                "Combining Kanban task workflows with tactile sticky notes gives the ultimate productivity blend!\n\n• Front-end: Tactile Memphis Pop UI with bold borders and hard offset drop shadows\n• Back-end: Spring Boot 3 + Spring Data JPA + Thymeleaf\n• Database: In-memory H2 with zero local installation required.",
                "yellow", true, "Architecture");

        createNote(demo, sprintBoard, "🚀 Sprint Demo Checklist",
                "Key presentation beats to showcase:\n1. Drag & drop task transitions with optimistic state updates\n2. Celebratory full-window confetti burst on 'Done'\n3. Dedicated Sticky Notes section with instant keyword search\n4. Pin & unpin high-priority notes\n5. Fast board-specific note attachment",
                "pink", true, "Checklist");

        createNote(demo, sprintBoard, "🎨 Playful Color Palette Reference",
                "Memphis Modern Digital Tokens:\n• Primary Violet: #8B5CF6\n• Secondary Pink: #F472B6\n• Tertiary Yellow: #FBBF24\n• Quaternary Mint: #34D399\n• Sky Blue: #38BDF8\n• Hard Pop Shadows: 4px 4px 0px #1E293B",
                "mint", false, "Design");

        createNote(demo, marketingBoard, "🎯 Growth Ideas & Launch Channels",
                "1. Showcase interactive confetti clip on Product Hunt\n2. Share micro-animations and tactile candy button design on X / LinkedIn\n3. Write technical deep dive: 'Full-Stack Spring Boot 3 Kanban & Notes App'",
                "peach", false, "Growth");

        createNote(demo, null, "⚡ Quick Brainstorm & Meeting Snippets",
                "Standup highlights:\n• Keep board workflow clean while offering quick-access stickies\n• Add color badges for rapid visual scanning\n• Pin important brainstorm ideas right at the top!",
                "blue", false, "Brainstorm");

        // Seed Note for Admin
        createNote(admin, adminBoard, "🔐 Security & Infrastructure Checklist",
                "1. Rotate production database credentials quarterly\n2. Monitor BCrypt CPU consumption on spike loads\n3. Review Spring Security 6 CSRF configurations on REST endpoints\n4. Schedule automated nightly backups.",
                "violet", true, "Security");

        logger.info("Successfully seeded database with admin (admin/admin123) and demo (demo/password123) users!");
    }

    private void createNote(User user, Board board, String title, String content, String color, boolean pinned, String category) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setColor(color);
        note.setPinned(pinned);
        note.setCategory(category);
        note.setUser(user);
        note.setBoard(board);
        noteRepository.save(note);
    }

    private void createTask(Board board, String title, String description, TaskStatus status,
                            TaskPriority priority, LocalDate dueDate, int position) {
        Task task = new Task(title, description, status, priority, dueDate, board);
        task.setPosition(position);
        taskRepository.save(task);
    }
}
