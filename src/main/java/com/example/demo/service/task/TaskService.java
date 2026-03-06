package com.example.demo.service.task;

import com.example.demo.dto.task.*;
import com.example.demo.repositories.task.TaskEntity;
import com.example.demo.repositories.task.TaskRepository;
import com.example.demo.repositories.user.UserEntity;
import com.example.demo.repositories.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    @Transactional
    public TaskResponse createTask(CreateTaskRequest taskRequest, String username) {

        UserEntity author = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found username:" + username));

        UserEntity assignee = null;
        if (taskRequest.assigneeId() != null) {
            assignee = userRepository.findById(taskRequest.assigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
        }

        var taskToSave = taskMapper.toEntity(taskRequest, author, assignee);
        var savedTask = taskRepository.save(taskToSave);
        return taskMapper.toDomain(savedTask);
    }

    public TaskResponse getTaskById(Long id) {

        return taskMapper.toDomain(taskRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Task not found id:" + id)));
    }

    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest updateTaskRequest, String username, boolean isAdmin) {
        TaskEntity taskEntity = getTaskIfAuthorOrAdmin(id, username, isAdmin);

        taskEntity.setTitle(updateTaskRequest.title());
        taskEntity.setDescription(updateTaskRequest.description());
        taskEntity.setStatus(updateTaskRequest.status());
        taskEntity.setPriority(updateTaskRequest.priority());
        if (updateTaskRequest.assigneeId() != null) {
            taskEntity.setAssignee(userRepository.
                    findById(updateTaskRequest.assigneeId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Assignee not found id:" + updateTaskRequest.assigneeId()
                    )));
        } else {
            taskEntity.setAssignee(null);
        }

        var updatedTask = taskRepository.save(taskEntity);

        return taskMapper.toDomain(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id, String username, boolean isAdmin) {
        TaskEntity taskEntity = getTaskIfAuthorOrAdmin(id, username, isAdmin);
        taskRepository.delete(taskEntity);
    }

    private TaskEntity getTaskIfAuthorOrAdmin(Long id, String username, boolean isAdmin) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found id:" + id));

        UserEntity currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found username:" + username));

        if (!currentUser.getId().equals(task.getAuthor().getId()) && !isAdmin) {
            throw new AccessDeniedException("Access denied");
        }

        return task;
    }

    public List<TaskResponse> searchAllTasksByFilter(TasksSearchFilter filter) {
        List<TaskEntity> allTasks = taskRepository.
                searchAllByFilter(filter.status(), filter.assigneeId(), filter.authorId());

        return allTasks.stream().map(taskMapper::toDomain).toList();
    }
}
