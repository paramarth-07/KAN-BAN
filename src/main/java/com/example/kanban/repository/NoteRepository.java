package com.example.kanban.repository;

import com.example.kanban.model.Board;
import com.example.kanban.model.Note;
import com.example.kanban.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserOrderByPinnedDescUpdatedAtDesc(User user);

    List<Note> findByUserAndBoardOrderByPinnedDescUpdatedAtDesc(User user, Board board);

    List<Note> findByUserAndCategoryOrderByPinnedDescUpdatedAtDesc(User user, String category);

    @Query("SELECT n FROM Note n WHERE n.user = :user AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(n.category) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY n.pinned DESC, n.updatedAt DESC")
    List<Note> searchNotes(@Param("user") User user, @Param("query") String query);

    @Query("SELECT n FROM Note n WHERE n.user = :user AND n.board.id = :boardId ORDER BY n.pinned DESC, n.updatedAt DESC")
    List<Note> findByUserAndBoardId(@Param("user") User user, @Param("boardId") Long boardId);

    @Query("SELECT DISTINCT n.category FROM Note n WHERE n.user = :user AND n.category IS NOT NULL AND n.category != '' ORDER BY n.category ASC")
    List<String> findDistinctCategoriesByUser(@Param("user") User user);

    long countByUser(User user);

    long countByUserAndPinnedTrue(User user);

    List<Note> findAllByOrderByPinnedDescUpdatedAtDesc();
}
