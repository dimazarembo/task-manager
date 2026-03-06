package com.example.demo.dto.task;

import com.example.demo.repositories.task.TaskPriority;
import com.example.demo.repositories.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @NotBlank(message = "title is required")
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,
        String description,
        @NotNull(message = "status is required")
        TaskStatus status,
        @NotNull(message = "priority is required")
        TaskPriority priority,
        @Positive(message = "assigneeId must be positive")
        Long assigneeId
) {
}
