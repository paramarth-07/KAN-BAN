package com.example.kanban.service;

import com.example.kanban.dto.NoteDto;
import com.example.kanban.exception.ResourceNotFoundException;
import com.example.kanban.exception.UnauthorizedAccessException;
import com.example.kanban.model.Board;
import com.example.kanban.model.Note;
import com.example.kanban.model.Role;
import com.example.kanban.model.User;
import com.example.kanban.repository.BoardRepository;
import com.example.kanban.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final BoardRepository boardRepository;

    public NoteService(NoteRepository noteRepository, BoardRepository boardRepository) {
        this.noteRepository = noteRepository;
        this.boardRepository = boardRepository;
    }

    @Transactional(readOnly = true)
    public List<Note> getNotesForUser(User user, String search, String category, Long boardId) {
        if (search != null && !search.trim().isEmpty()) {
            return noteRepository.searchNotes(user, search.trim());
        }
        if (category != null && !category.trim().isEmpty()) {
            return noteRepository.findByUserAndCategoryOrderByPinnedDescUpdatedAtDesc(user, category.trim());
        }
        if (boardId != null) {
            return noteRepository.findByUserAndBoardId(user, boardId);
        }
        return noteRepository.findByUserOrderByPinnedDescUpdatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Note> getNotesForBoard(Board board, User user) {
        return noteRepository.findByUserAndBoardOrderByPinnedDescUpdatedAtDesc(user, board);
    }

    @Transactional(readOnly = true)
    public Note getNoteByIdAndUser(Long noteId, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        validateOwnershipOrAdmin(note, user);
        return note;
    }

    public Note createNote(NoteDto dto, User user) {
        Note note = new Note();
        note.setTitle(dto.getTitle().trim());
        note.setContent(dto.getContent().trim());
        note.setColor((dto.getColor() != null && !dto.getColor().isBlank()) ? dto.getColor().trim() : "yellow");
        note.setPinned(dto.isPinned());
        note.setCategory((dto.getCategory() != null && !dto.getCategory().isBlank()) ? dto.getCategory().trim() : null);
        note.setUser(user);

        if (dto.getBoardId() != null) {
            Board board = boardRepository.findById(dto.getBoardId())
                    .orElse(null);
            if (board != null && (board.getUser().getId().equals(user.getId()) || user.getRole() == Role.ROLE_ADMIN)) {
                note.setBoard(board);
            }
        }

        return noteRepository.save(note);
    }

    public Note updateNote(Long noteId, NoteDto dto, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        validateOwnershipOrAdmin(note, user);

        note.setTitle(dto.getTitle().trim());
        note.setContent(dto.getContent().trim());
        if (dto.getColor() != null && !dto.getColor().isBlank()) {
            note.setColor(dto.getColor().trim());
        }
        note.setPinned(dto.isPinned());
        note.setCategory((dto.getCategory() != null && !dto.getCategory().isBlank()) ? dto.getCategory().trim() : null);

        if (dto.getBoardId() != null) {
            Board board = boardRepository.findById(dto.getBoardId()).orElse(null);
            if (board != null && (board.getUser().getId().equals(user.getId()) || user.getRole() == Role.ROLE_ADMIN)) {
                note.setBoard(board);
            } else {
                note.setBoard(null);
            }
        } else {
            note.setBoard(null);
        }

        return noteRepository.save(note);
    }

    public Note togglePin(Long noteId, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        validateOwnershipOrAdmin(note, user);
        note.setPinned(!note.isPinned());
        return noteRepository.save(note);
    }

    public void deleteNote(Long noteId, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        validateOwnershipOrAdmin(note, user);
        noteRepository.delete(note);
    }

    @Transactional(readOnly = true)
    public long countNotes(User user) {
        return noteRepository.countByUser(user);
    }

    @Transactional(readOnly = true)
    public long countPinned(User user) {
        return noteRepository.countByUserAndPinnedTrue(user);
    }

    @Transactional(readOnly = true)
    public List<String> getCategories(User user) {
        return noteRepository.findDistinctCategoriesByUser(user);
    }

    private void validateOwnershipOrAdmin(Note note, User user) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return;
        }
        if (!note.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You do not have access to note ID: " + note.getId());
        }
    }
}
