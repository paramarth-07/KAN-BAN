package com.example.kanban.service;

import com.example.kanban.dto.BoardDto;
import com.example.kanban.exception.ResourceNotFoundException;
import com.example.kanban.exception.UnauthorizedAccessException;
import com.example.kanban.model.Board;
import com.example.kanban.model.Role;
import com.example.kanban.model.User;
import com.example.kanban.repository.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional(readOnly = true)
    public List<Board> getBoardsForUser(User user) {
        return boardRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public Board getBoardById(Long id) {
        return boardRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", id));
    }

    @Transactional(readOnly = true)
    public Board getBoardByIdAndUser(Long boardId, User user) {
        Board board = boardRepository.findByIdWithTasks(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardId));

        validateOwnershipOrAdmin(board, user);
        return board;
    }

    public Board createBoard(BoardDto dto, User user) {
        Board board = new Board();
        board.setTitle(dto.getTitle().trim());
        board.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        board.setUser(user);
        return boardRepository.save(board);
    }

    public Board updateBoard(Long boardId, BoardDto dto, User user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardId));

        validateOwnershipOrAdmin(board, user);

        board.setTitle(dto.getTitle().trim());
        board.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        return boardRepository.save(board);
    }

    public void deleteBoard(Long boardId, User user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardId));

        validateOwnershipOrAdmin(board, user);

        if (board.getNotes() != null) {
            for (com.example.kanban.model.Note note : board.getNotes()) {
                note.setBoard(null);
            }
        }

        boardRepository.delete(board);
    }

    @Transactional(readOnly = true)
    public List<Board> getAllBoards() {
        return boardRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public long countBoardsByUser(User user) {
        return boardRepository.countByUser(user);
    }

    private void validateOwnershipOrAdmin(Board board, User user) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return;
        }
        if (!board.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You do not have access to board ID: " + board.getId());
        }
    }
}
