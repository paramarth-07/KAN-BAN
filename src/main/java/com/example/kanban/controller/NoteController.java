package com.example.kanban.controller;

import com.example.kanban.dto.NoteDto;
import com.example.kanban.model.Board;
import com.example.kanban.model.Note;
import com.example.kanban.model.User;
import com.example.kanban.security.CustomUserDetails;
import com.example.kanban.service.BoardService;
import com.example.kanban.service.NoteService;
import com.example.kanban.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;
    private final BoardService boardService;
    private final UserService userService;

    public NoteController(NoteService noteService, BoardService boardService, UserService userService) {
        this.noteService = noteService;
        this.boardService = boardService;
        this.userService = userService;
    }

    private User getCurrentUser(CustomUserDetails userDetails) {
        return userService.findById(userDetails.getId());
    }

    @GetMapping
    public String listNotes(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) Long boardId,
                            Model model) {
        User currentUser = getCurrentUser(userDetails);
        List<Note> notes = noteService.getNotesForUser(currentUser, search, category, boardId);
        List<Board> boards = boardService.getBoardsForUser(currentUser);
        List<String> categories = noteService.getCategories(currentUser);

        model.addAttribute("notes", notes);
        model.addAttribute("boards", boards);
        model.addAttribute("categories", categories);
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentBoardId", boardId);
        model.addAttribute("totalNotes", noteService.countNotes(currentUser));
        model.addAttribute("pinnedNotesCount", noteService.countPinned(currentUser));

        if (!model.containsAttribute("newNote")) {
            NoteDto newNote = new NoteDto();
            if (boardId != null) {
                newNote.setBoardId(boardId);
            }
            if (category != null && !category.isBlank()) {
                newNote.setCategory(category);
            }
            model.addAttribute("newNote", newNote);
        }

        model.addAttribute("user", currentUser);
        return "note/list";
    }

    @PostMapping
    public String createNote(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @Valid @ModelAttribute("newNote") NoteDto noteDto,
                             BindingResult bindingResult,
                             @RequestParam(required = false) String returnUrl,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        User currentUser = getCurrentUser(userDetails);

        if (bindingResult.hasErrors()) {
            List<Note> notes = noteService.getNotesForUser(currentUser, null, null, null);
            List<Board> boards = boardService.getBoardsForUser(currentUser);
            List<String> categories = noteService.getCategories(currentUser);
            model.addAttribute("notes", notes);
            model.addAttribute("boards", boards);
            model.addAttribute("categories", categories);
            model.addAttribute("totalNotes", noteService.countNotes(currentUser));
            model.addAttribute("pinnedNotesCount", noteService.countPinned(currentUser));
            model.addAttribute("user", currentUser);
            model.addAttribute("showCreateModal", true);
            return "note/list";
        }

        noteService.createNote(noteDto, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Sticky note created successfully!");

        if (returnUrl != null && !returnUrl.isBlank()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/notes";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        User currentUser = getCurrentUser(userDetails);
        Note note = noteService.getNoteByIdAndUser(id, currentUser);
        List<Board> boards = boardService.getBoardsForUser(currentUser);

        NoteDto noteDto = new NoteDto(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getColor(),
                note.isPinned(),
                note.getCategory(),
                note.getBoard() != null ? note.getBoard().getId() : null
        );

        model.addAttribute("note", noteDto);
        model.addAttribute("noteId", id);
        model.addAttribute("boards", boards);
        return "note/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateNote(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             @Valid @ModelAttribute("note") NoteDto noteDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        User currentUser = getCurrentUser(userDetails);

        if (bindingResult.hasErrors()) {
            List<Board> boards = boardService.getBoardsForUser(currentUser);
            model.addAttribute("noteId", id);
            model.addAttribute("boards", boards);
            return "note/edit";
        }

        noteService.updateNote(id, noteDto, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Note updated successfully!");
        return "redirect:/notes";
    }

    @PostMapping("/{id}/pin")
    public String togglePin(@PathVariable Long id,
                            @RequestParam(required = false) String returnUrl,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);
        Note note = noteService.togglePin(id, currentUser);
        redirectAttributes.addFlashAttribute("successMessage",
                note.isPinned() ? "Note pinned to top!" : "Note unpinned.");

        if (returnUrl != null && !returnUrl.isBlank()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/notes";
    }

    @PostMapping("/{id}/delete")
    public String deleteNote(@PathVariable Long id,
                             @RequestParam(required = false) String returnUrl,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);
        noteService.deleteNote(id, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Note deleted successfully.");

        if (returnUrl != null && !returnUrl.isBlank()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/notes";
    }
}
