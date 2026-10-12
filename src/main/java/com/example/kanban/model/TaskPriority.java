package com.example.kanban.model;

public enum TaskPriority {
    LOW("Low", "badge-priority-low"),
    MEDIUM("Medium", "badge-priority-medium"),
    HIGH("High", "badge-priority-high"),
    URGENT("Urgent", "badge-priority-urgent");

    private final String displayName;
    private final String badgeClass;

    TaskPriority(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
