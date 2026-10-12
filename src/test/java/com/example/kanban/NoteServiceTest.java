package com.example.kanban;

import com.example.kanban.dto.BoardDto;
import com.example.kanban.dto.NoteDto;
import com.example.kanban.exception.UnauthorizedAccessException;
import com.example.kanban.model.Board;
import com.example.kanban.model.Note;
import com.example.kanban.model.User;
import com.example.kanban.service.BoardService;
import com.example.kanban.service.NoteService;
import com.example.kanban.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class NoteServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private NoteService noteService;

    private User demoUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        demoUser = userService.findByUsername("demo");
        adminUser = userService.findByUsername("admin");
    }

    @Test
    void testCreateAndRetrieveNote() {
        NoteDto dto = new NoteDto(null, "Test Architecture Sticky", "Testing note content", "pink", false, "Arch", null);
        Note note = noteService.createNote(dto, demoUser);

        assertNotNull(note.getId());
        assertEquals("Test Architecture Sticky", note.getTitle());
        assertEquals("Testing note content", note.getContent());
        assertEquals("pink", note.getColor());
        assertFalse(note.isPinned());

        Note retrieved = noteService.getNoteByIdAndUser(note.getId(), demoUser);
        assertEquals(note.getTitle(), retrieved.getTitle());
    }

    @Test
    void testUserCannotAccessAnotherUsersNote() {
        NoteDto dto = new NoteDto(null, "Admin Private Note", "Secret", "violet", false, "Security", null);
        Note adminNote = noteService.createNote(dto, adminUser);

        assertThrows(UnauthorizedAccessException.class, () -> {
            noteService.getNoteByIdAndUser(adminNote.getId(), demoUser);
        });
    }

    @Test
    void testAdminCanAccessAnyNote() {
        NoteDto dto = new NoteDto(null, "Demo Public Note", "Public", "yellow", false, "General", null);
        Note demoNote = noteService.createNote(dto, demoUser);

        Note retrievedByAdmin = noteService.getNoteByIdAndUser(demoNote.getId(), adminUser);
        assertNotNull(retrievedByAdmin);
        assertEquals(demoNote.getId(), retrievedByAdmin.getId());
    }

    @Test
    void testTogglePin() {
        NoteDto dto = new NoteDto(null, "Pin Test Note", "Testing pin", "yellow", false, null, null);
        Note note = noteService.createNote(dto, demoUser);
        assertFalse(note.isPinned());

        Note pinned = noteService.togglePin(note.getId(), demoUser);
        assertTrue(pinned.isPinned());

        Note unpinned = noteService.togglePin(note.getId(), demoUser);
        assertFalse(unpinned.isPinned());
    }

    @Test
    void testAttachNoteToBoard() {
        BoardDto bDto = new BoardDto(null, "Note Test Board", "Board for notes");
        Board board = boardService.createBoard(bDto, demoUser);

        NoteDto nDto = new NoteDto(null, "Board Attached Sticky", "Sprint notes", "mint", false, "Sprint", board.getId());
        Note note = noteService.createNote(nDto, demoUser);

        assertNotNull(note.getBoard());
        assertEquals(board.getId(), note.getBoard().getId());

        List<Note> boardNotes = noteService.getNotesForBoard(board, demoUser);
        assertTrue(boardNotes.stream().anyMatch(n -> n.getId().equals(note.getId())));
    }
}
