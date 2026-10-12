package com.example.kanban.model;

public enum TaskStatus {
    TODO("To Do", "status-todo"),
    IN_PROGRESS("In Progress", "status-in-progress"),
    DONE("Done", "status-done");

    private final String displayName;
    private final String cssClass;

    TaskStatus(String displayName, String cssClass) {
        this.displayName = displayName;
        this.cssClass = cssClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCssClass() {
        return cssClass;
    }
}
