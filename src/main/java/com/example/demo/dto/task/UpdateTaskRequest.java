package com.example.demo.dto.task;

import com.example.demo.repositories.task.TaskPriority;
import com.example.demo.repositories.task.TaskStatus;

public record UpdateTaskRequest(
     String title,
     String description,
     TaskStatus taskStatus,
     TaskPriority taskPriority,
     Long assigneeId
) {
}
