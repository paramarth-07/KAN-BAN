package com.example.kanban.controller;

import com.example.kanban.dto.BoardDto;
import com.example.kanban.dto.TaskDto;
import com.example.kanban.model.Board;
import com.example.kanban.model.Task;
import com.example.kanban.model.TaskStatus;
import com.example.kanban.model.User;
import com.example.kanban.security.CustomUserDetails;
import com.example.kanban.service.BoardService;
import com.example.kanban.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kanban.dto.NoteDto;
import com.example.kanban.model.Note;
import com.example.kanban.service.NoteService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;
    private final NoteService noteService;

    public BoardController(BoardService boardService, UserService userService, NoteService noteService) {
        this.boardService = boardService;
        this.userService = userService;
        this.noteService = noteService;
    }

    private User getCurrentUser(CustomUserDetails userDetails) {
        return userService.findById(userDetails.getId());
    }

    @GetMapping
    public String listBoards(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User currentUser = getCurrentUser(userDetails);
        List<Board> boards = boardService.getBoardsForUser(currentUser);

        model.addAttribute("boards", boards);
        if (!model.containsAttribute("newBoard")) {
            model.addAttribute("newBoard", new BoardDto());
        }
        model.addAttribute("user", currentUser);
        return "board/list";
    }

    @PostMapping
    public String createBoard(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @Valid @ModelAttribute("newBoard") BoardDto boardDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        User currentUser = getCurrentUser(userDetails);

        if (bindingResult.hasErrors()) {
            List<Board> boards = boardService.getBoardsForUser(currentUser);
            model.addAttribute("boards", boards);
            model.addAttribute("user", currentUser);
            model.addAttribute("showCreateModal", true);
            return "board/list";
        }

        Board created = boardService.createBoard(boardDto, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Board '" + created.getTitle() + "' created successfully!");
        return "redirect:/boards/" + created.getId();
    }

    @GetMapping("/{id}")
    public String viewBoard(@PathVariable Long id,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {
        User currentUser = getCurrentUser(userDetails);
        Board board = boardService.getBoardByIdAndUser(id, currentUser);

        List<Task> todoTasks = board.getTasks().stream()
                .filter(t -> t.getStatus() == TaskStatus.TODO)
                .collect(Collectors.toList());

        List<Task> inProgressTasks = board.getTasks().stream()
                .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS)
                .collect(Collectors.toList());

        List<Task> doneTasks = board.getTasks().stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .collect(Collectors.toList());

        model.addAttribute("board", board);
        model.addAttribute("todoTasks", todoTasks);
        model.addAttribute("inProgressTasks", inProgressTasks);
        model.addAttribute("doneTasks", doneTasks);

        List<Note> boardNotes = noteService.getNotesForBoard(board, currentUser);
        model.addAttribute("boardNotes", boardNotes);

        if (!model.containsAttribute("newTask")) {
            model.addAttribute("newTask", new TaskDto());
        }

        if (!model.containsAttribute("newNote")) {
            NoteDto newNote = new NoteDto();
            newNote.setBoardId(board.getId());
            model.addAttribute("newNote", newNote);
        }

        BoardDto editBoardDto = new BoardDto(board.getId(), board.getTitle(), board.getDescription());
        model.addAttribute("editBoard", editBoardDto);

        model.addAttribute("currentUser", currentUser);
        return "board/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        User currentUser = getCurrentUser(userDetails);
        Board board = boardService.getBoardByIdAndUser(id, currentUser);

        BoardDto boardDto = new BoardDto(board.getId(), board.getTitle(), board.getDescription());
        model.addAttribute("board", boardDto);
        model.addAttribute("boardId", id);
        return "board/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateBoard(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              @Valid @ModelAttribute("board") BoardDto boardDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        User currentUser = getCurrentUser(userDetails);

        if (bindingResult.hasErrors()) {
            model.addAttribute("boardId", id);
            return "board/edit";
        }

        boardService.updateBoard(id, boardDto, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Board updated successfully!");
        return "redirect:/boards/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);
        boardService.deleteBoard(id, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Board deleted successfully.");
        return "redirect:/boards";
    }
}
