package com.example.demo.dto.task;

public record TasksSearchFilter(String status, Long assigneeId, Long authorId) {
}
