package com.example.kanban;

import com.example.kanban.model.Board;
import com.example.kanban.model.Task;
import com.example.kanban.model.User;
import com.example.kanban.repository.BoardRepository;
import com.example.kanban.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @WithUserDetails("demo")
    void testAsyncStatusUpdateWithCsrf() throws Exception {
        User demo = userRepository.findByUsername("demo").orElseThrow();
        List<Board> boards = boardRepository.findByUserOrderByCreatedAtDesc(demo);
        assertFalse(boards.isEmpty(), "Demo user should have pre-seeded boards");

        Board board = boards.get(0);
        assertFalse(board.getTasks().isEmpty(), "Board should have pre-seeded tasks");
        Task task = board.getTasks().get(0);

        String jsonPayload = """
                {
                    "status": "DONE",
                    "position": 1
                }
                """;

        mockMvc.perform(post("/api/tasks/" + task.getId() + "/status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.taskId").value(task.getId()))
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.statusDisplayName").value("Done"));
    }
}
