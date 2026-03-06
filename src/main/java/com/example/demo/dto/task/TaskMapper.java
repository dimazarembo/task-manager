package com.example.demo.dto.task;

import com.example.demo.repositories.task.TaskEntity;
import com.example.demo.repositories.task.TaskStatus;
import com.example.demo.repositories.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskEntity toEntity(CreateTaskRequest taskRequest, UserEntity author, UserEntity assignee) {

        return TaskEntity.builder().title(taskRequest.title())
                .description(taskRequest.description())
                .status(TaskStatus.TODO)
                .priority(taskRequest.priority())
                .author(author).assignee(assignee).build();
    }

    public TaskResponse toDomain(TaskEntity savedTask) {
        Long assigneeId = savedTask.getAssignee() != null ? savedTask.getAssignee().getId() : null;
        return new TaskResponse(
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getStatus(),
                savedTask.getPriority(),
                savedTask.getAuthor().getId(),
                assigneeId,
                savedTask.getCreatedAt(),
                savedTask.getUpdatedAt()
        );
    }
}
