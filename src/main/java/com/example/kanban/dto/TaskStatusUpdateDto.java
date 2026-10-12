package com.example.kanban.dto;

import com.example.kanban.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

public class TaskStatusUpdateDto {

    @NotNull(message = "New status is required")
    private TaskStatus status;

    private Integer position;

    public TaskStatusUpdateDto() {
    }

    public TaskStatusUpdateDto(TaskStatus status, Integer position) {
        this.status = status;
        this.position = position;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
