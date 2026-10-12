package com.example.kanban.controller;

import com.example.kanban.dto.TaskStatusUpdateDto;
import com.example.kanban.model.Task;
import com.example.kanban.model.User;
import com.example.kanban.security.CustomUserDetails;
import com.example.kanban.service.TaskService;
import com.example.kanban.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskApiController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping("/{taskId}/status")
    public ResponseEntity<Map<String, Object>> updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusUpdateDto updateDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User currentUser = userService.findById(userDetails.getId());
        Task updated = taskService.updateTaskStatus(taskId, updateDto.getStatus(), updateDto.getPosition(), currentUser);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("taskId", updated.getId());
        response.put("status", updated.getStatus().name());
        response.put("statusDisplayName", updated.getStatus().getDisplayName());
        response.put("message", "Task successfully updated to " + updated.getStatus().getDisplayName());

        return ResponseEntity.ok(response);
    }
}
