package com.example.kanban;

import com.example.kanban.dto.BoardDto;
import com.example.kanban.dto.TaskDto;
import com.example.kanban.exception.UnauthorizedAccessException;
import com.example.kanban.model.*;
import com.example.kanban.service.BoardService;
import com.example.kanban.service.TaskService;
import com.example.kanban.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BoardAndTaskServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private TaskService taskService;

    private User demoUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        demoUser = userService.findByUsername("demo");
        adminUser = userService.findByUsername("admin");
    }

    @Test
    void testCreateAndRetrieveBoard() {
        BoardDto dto = new BoardDto(null, "Test Architecture Sprint", "Testing service layer");
        Board board = boardService.createBoard(dto, demoUser);

        assertNotNull(board.getId());
        assertEquals("Test Architecture Sprint", board.getTitle());
        assertEquals(demoUser.getId(), board.getUser().getId());

        Board retrieved = boardService.getBoardByIdAndUser(board.getId(), demoUser);
        assertEquals(board.getTitle(), retrieved.getTitle());
    }

    @Test
    void testUserCannotAccessAnotherUsersBoard() {
        // Create board owned by admin
        BoardDto dto = new BoardDto(null, "Admin Private Board", "Classified info");
        Board adminBoard = boardService.createBoard(dto, adminUser);

        // Demo user should NOT be able to access it
        assertThrows(UnauthorizedAccessException.class, () -> {
            boardService.getBoardByIdAndUser(adminBoard.getId(), demoUser);
        });
    }

    @Test
    void testAdminCanAccessAnyBoard() {
        // Create board owned by demo user
        BoardDto dto = new BoardDto(null, "Demo User Public Sprint", "Public info");
        Board demoBoard = boardService.createBoard(dto, demoUser);

        // Admin user CAN access it
        Board accessedByAdmin = boardService.getBoardByIdAndUser(demoBoard.getId(), adminUser);
        assertNotNull(accessedByAdmin);
        assertEquals(demoBoard.getId(), accessedByAdmin.getId());
    }

    @Test
    void testCreateTaskAndStatusTransitions() {
        BoardDto bDto = new BoardDto(null, "Task Flow Board", "Testing tasks");
        Board board = boardService.createBoard(bDto, demoUser);

        TaskDto tDto = new TaskDto(null, "Implement Playful UI", "Make it tactile",
                TaskStatus.TODO, TaskPriority.URGENT, LocalDate.now().plusDays(3));
        Task createdTask = taskService.createTask(board.getId(), tDto, demoUser);

        assertNotNull(createdTask.getId());
        assertEquals(TaskStatus.TODO, createdTask.getStatus());
        assertEquals(TaskPriority.URGENT, createdTask.getPriority());

        // Step forward: TODO -> IN_PROGRESS
        Task stepped1 = taskService.stepTaskStatus(createdTask.getId(), true, demoUser);
        assertEquals(TaskStatus.IN_PROGRESS, stepped1.getStatus());

        // Step forward: IN_PROGRESS -> DONE
        Task stepped2 = taskService.stepTaskStatus(createdTask.getId(), true, demoUser);
        assertEquals(TaskStatus.DONE, stepped2.getStatus());

        // Step backward: DONE -> IN_PROGRESS
        Task steppedBack = taskService.stepTaskStatus(createdTask.getId(), false, demoUser);
        assertEquals(TaskStatus.IN_PROGRESS, steppedBack.getStatus());
    }

    @Test
    void testUpdateTaskStatusDirectly() {
        BoardDto bDto = new BoardDto(null, "Status Direct Board", "Testing direct status");
        Board board = boardService.createBoard(bDto, demoUser);

        TaskDto tDto = new TaskDto(null, "Direct Status Test", "Desc",
                TaskStatus.TODO, TaskPriority.LOW, null);
        Task createdTask = taskService.createTask(board.getId(), tDto, demoUser);

        Task updated = taskService.updateTaskStatus(createdTask.getId(), TaskStatus.DONE, 5, demoUser);
        assertEquals(TaskStatus.DONE, updated.getStatus());
        assertEquals(5, updated.getPosition());
    }
}
