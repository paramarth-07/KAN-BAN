package com.example.kanban.controller;

import com.example.kanban.dto.TaskDto;
import com.example.kanban.model.Task;
import com.example.kanban.model.User;
import com.example.kanban.security.CustomUserDetails;
import com.example.kanban.service.TaskService;
import com.example.kanban.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/boards/{boardId}/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    private User getCurrentUser(CustomUserDetails userDetails) {
        return userService.findById(userDetails.getId());
    }

    @PostMapping
    public String createTask(@PathVariable Long boardId,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             @Valid @ModelAttribute("newTask") TaskDto taskDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newTask", bindingResult);
            redirectAttributes.addFlashAttribute("newTask", taskDto);
            redirectAttributes.addFlashAttribute("showCreateTaskModal", true);
            redirectAttributes.addFlashAttribute("errorMessage", "Please fix the validation errors in the task form.");
            return "redirect:/boards/" + boardId;
        }

        taskService.createTask(boardId, taskDto, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Task '" + taskDto.getTitle() + "' created successfully!");
        return "redirect:/boards/" + boardId;
    }

    @GetMapping("/{taskId}/edit")
    public String showEditTaskForm(@PathVariable Long boardId,
                                   @PathVariable Long taskId,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   Model model) {
        User currentUser = getCurrentUser(userDetails);
        Task task = taskService.getTaskByIdAndUser(boardId, taskId, currentUser);

        TaskDto taskDto = new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate()
        );

        model.addAttribute("task", taskDto);
        model.addAttribute("boardId", boardId);
        model.addAttribute("taskId", taskId);
        return "board/task-edit";
    }

    @PostMapping("/{taskId}/edit")
    public String updateTask(@PathVariable Long boardId,
                             @PathVariable Long taskId,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             @Valid @ModelAttribute("task") TaskDto taskDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        User currentUser = getCurrentUser(userDetails);

        if (bindingResult.hasErrors()) {
            model.addAttribute("boardId", boardId);
            model.addAttribute("taskId", taskId);
            return "board/task-edit";
        }

        taskService.updateTask(boardId, taskId, taskDto, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Task updated successfully!");
        return "redirect:/boards/" + boardId;
    }

    @PostMapping("/{taskId}/step")
    public String stepTaskStatus(@PathVariable Long boardId,
                                 @PathVariable Long taskId,
                                 @RequestParam(value = "forward", defaultValue = "true") boolean forward,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);
        Task updated = taskService.stepTaskStatus(taskId, forward, currentUser);
        if (updated.getStatus() == com.example.kanban.model.TaskStatus.DONE) {
            redirectAttributes.addFlashAttribute("taskDoneCelebration", true);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Task status moved to " + updated.getStatus().getDisplayName());
        return "redirect:/boards/" + boardId;
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable Long boardId,
                             @PathVariable Long taskId,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);
        taskService.deleteTask(boardId, taskId, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully.");
        return "redirect:/boards/" + boardId;
    }
}
