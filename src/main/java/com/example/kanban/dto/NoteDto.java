package com.example.kanban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NoteDto {

    private Long id;

    @NotBlank(message = "Note title is required")
    @Size(max = 120, message = "Title cannot exceed 120 characters")
    private String title;

    @NotBlank(message = "Note content is required")
    private String content;

    private String color = "yellow";

    private boolean pinned = false;

    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;

    private Long boardId;

    public NoteDto() {
    }

    public NoteDto(Long id, String title, String content, String color, boolean pinned, String category, Long boardId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.color = (color != null && !color.isBlank()) ? color : "yellow";
        this.pinned = pinned;
        this.category = category;
        this.boardId = boardId;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }
}
