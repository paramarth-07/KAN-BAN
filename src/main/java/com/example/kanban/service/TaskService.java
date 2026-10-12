package com.example.kanban.service;

import com.example.kanban.dto.TaskDto;
import com.example.kanban.exception.ResourceNotFoundException;
import com.example.kanban.exception.UnauthorizedAccessException;
import com.example.kanban.model.*;
import com.example.kanban.repository.BoardRepository;
import com.example.kanban.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;

    public TaskService(TaskRepository taskRepository, BoardRepository boardRepository) {
        this.taskRepository = taskRepository;
        this.boardRepository = boardRepository;
    }

    public Task createTask(Long boardId, TaskDto dto, User user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardId));

        validateBoardAccess(board, user);

        Task task = new Task();
        task.setTitle(dto.getTitle().trim());
        task.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        task.setStatus(dto.getStatus() != null ? dto.getStatus() : TaskStatus.TODO);
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : TaskPriority.MEDIUM);
        task.setDueDate(dto.getDueDate());
        task.setBoard(board);

        // Position at the end of the status column
        long countInStatus = taskRepository.countByBoardIdAndStatus(boardId, task.getStatus());
        task.setPosition((int) countInStatus);

        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Task getTaskByIdAndUser(Long boardId, Long taskId, User user) {
        Task task = taskRepository.findByIdAndBoardId(taskId, boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        validateBoardAccess(task.getBoard(), user);
        return task;
    }

    public Task updateTask(Long boardId, Long taskId, TaskDto dto, User user) {
        Task task = getTaskByIdAndUser(boardId, taskId, user);

        task.setTitle(dto.getTitle().trim());
        task.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());

        return taskRepository.save(task);
    }

    public Task updateTaskStatus(Long taskId, TaskStatus newStatus, Integer position, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        validateBoardAccess(task.getBoard(), user);

        task.setStatus(newStatus);
        if (position != null) {
            task.setPosition(position);
        }

        return taskRepository.save(task);
    }

    public Task stepTaskStatus(Long taskId, boolean moveForward, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        validateBoardAccess(task.getBoard(), user);

        TaskStatus current = task.getStatus();
        TaskStatus next = current;

        if (moveForward) {
            if (current == TaskStatus.TODO) {
                next = TaskStatus.IN_PROGRESS;
            } else if (current == TaskStatus.IN_PROGRESS) {
                next = TaskStatus.DONE;
            }
        } else {
            if (current == TaskStatus.DONE) {
                next = TaskStatus.IN_PROGRESS;
            } else if (current == TaskStatus.IN_PROGRESS) {
                next = TaskStatus.TODO;
            }
        }

        task.setStatus(next);
        return taskRepository.save(task);
    }

    public void deleteTask(Long boardId, Long taskId, User user) {
        Task task = getTaskByIdAndUser(boardId, taskId, user);
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByBoard(Long boardId) {
        return taskRepository.findByBoardIdOrderByPositionAscIdAsc(boardId);
    }

    private void validateBoardAccess(Board board, User user) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return;
        }
        if (!board.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You do not have access to board ID: " + board.getId());
        }
    }
}
