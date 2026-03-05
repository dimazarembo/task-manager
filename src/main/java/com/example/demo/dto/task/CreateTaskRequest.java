package com.example.demo.dto.task;

import com.example.demo.repositories.task.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank(message = "title is required")
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,
        String description,
        @NotNull(message = "taskPriority is required")
        TaskPriority taskPriority,
        @Positive(message = "assigneeId must be positive")
        Long assigneeId
) {
}
