package com.example.kanban.repository;

import com.example.kanban.model.Task;
import com.example.kanban.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByBoardIdOrderByPositionAscIdAsc(Long boardId);

    List<Task> findByBoardIdAndStatusOrderByPositionAscIdAsc(Long boardId, TaskStatus status);

    Optional<Task> findByIdAndBoardId(Long id, Long boardId);

    long countByBoardId(Long boardId);

    long countByBoardIdAndStatus(Long boardId, TaskStatus status);
}
