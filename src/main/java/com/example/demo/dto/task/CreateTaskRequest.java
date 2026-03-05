package com.example.demo.dto.task;

import com.example.demo.repositories.task.TaskPriority;

public record CreateTaskRequest(
        String title,
        String description,
        TaskPriority taskPriority,
        Long assigneeId
) {
}
