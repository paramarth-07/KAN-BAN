package com.example.kanban.repository;

import com.example.kanban.model.Board;
import com.example.kanban.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByUserOrderByCreatedAtDesc(User user);

    List<Board> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Board> findByIdAndUser(Long id, User user);

    @Query("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.tasks WHERE b.id = :id")
    Optional<Board> findByIdWithTasks(@Param("id") Long id);

    List<Board> findAllByOrderByCreatedAtDesc();

    long countByUser(User user);
}
