package com.example.demo.dto.task;

import com.example.demo.repositories.task.TaskStatus;

public record TasksSearchFilter(TaskStatus status, Long assigneeId, Long authorId) {
}
