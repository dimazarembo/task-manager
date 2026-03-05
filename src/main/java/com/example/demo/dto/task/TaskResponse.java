package com.example.demo.dto.task;

import com.example.demo.repositories.task.TaskPriority;
import com.example.demo.repositories.task.TaskStatus;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus taskStatus,
        TaskPriority taskPriority,
        Long authorId,
        Long assigneeId,
        java.time.Instant createdDate) {
}
