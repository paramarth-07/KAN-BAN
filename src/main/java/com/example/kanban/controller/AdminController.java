package com.example.kanban.controller;

import com.example.kanban.model.Board;
import com.example.kanban.model.User;
import com.example.kanban.security.CustomUserDetails;
import com.example.kanban.service.BoardService;
import com.example.kanban.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final BoardService boardService;

    public AdminController(UserService userService, BoardService boardService) {
        this.userService = userService;
        this.boardService = boardService;
    }

    @GetMapping
    public String adminDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<User> users = userService.getAllUsers();
        List<Board> boards = boardService.getAllBoards();

        long totalTasks = boards.stream().mapToLong(b -> b.getTasks().size()).sum();
        long adminCount = users.stream().filter(u -> u.getRole().name().equals("ROLE_ADMIN")).count();

        model.addAttribute("users", users);
        model.addAttribute("boards", boards);
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalBoards", boards.size());
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("currentAdmin", userDetails != null ? userDetails.getUsername() : "admin");

        return "admin/dashboard";
    }

    @PostMapping("/users/{id}/toggle-role")
    public String toggleRole(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            String adminUsername = userDetails != null ? userDetails.getUsername() : "admin";
            userService.toggleUserRole(id, adminUsername);
            redirectAttributes.addFlashAttribute("successMessage", "User role toggled successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            String adminUsername = userDetails != null ? userDetails.getUsername() : "admin";
            userService.deleteUser(id, adminUsername);
            redirectAttributes.addFlashAttribute("successMessage", "User and all associated boards were deleted.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/boards/{id}/delete")
    public String deleteBoard(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        try {
            if (userDetails != null) {
                User admin = userService.findById(userDetails.getId());
                boardService.deleteBoard(id, admin);
                redirectAttributes.addFlashAttribute("successMessage", "Board ID " + id + " was deleted by administrator.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting board: " + e.getMessage());
        }
        return "redirect:/admin";
    }
}
