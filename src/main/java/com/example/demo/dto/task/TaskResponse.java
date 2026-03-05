package com.example.demo.dto.task;

import com.example.demo.repositories.task.TaskPriority;
import com.example.demo.repositories.task.TaskStatus;

import java.time.Instant;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus taskStatus,
        TaskPriority taskPriority,
        Long authorId,
        Long assigneeId,
        Instant createdDate,
        Instant updatedDate
) {
}
