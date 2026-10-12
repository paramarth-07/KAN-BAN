package com.example.kanban.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC, id ASC")
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    @OrderBy("pinned DESC, updatedAt DESC")
    private List<Note> notes = new ArrayList<>();

    public Board() {
    }

    public Board(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Helper methods for bidirectional task relationship
    public void addTask(Task task) {
        tasks.add(task);
        task.setBoard(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setBoard(null);
    }

    // Task count helpers
    public long getTaskCountByStatus(TaskStatus status) {
        return tasks.stream().filter(t -> t.getStatus() == status).count();
    }

    public long getTodoCount() {
        return getTaskCountByStatus(TaskStatus.TODO);
    }

    public long getInProgressCount() {
        return getTaskCountByStatus(TaskStatus.IN_PROGRESS);
    }

    public long getDoneCount() {
        return getTaskCountByStatus(TaskStatus.DONE);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
