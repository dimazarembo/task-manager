package com.example.demo.service.task;

import com.example.demo.dto.task.CreateTaskRequest;
import com.example.demo.dto.task.TaskMapper;
import com.example.demo.dto.task.TaskResponse;
import com.example.demo.dto.task.UpdateTaskRequest;
import com.example.demo.repositories.task.TaskEntity;
import com.example.demo.repositories.task.TaskPriority;
import com.example.demo.repositories.task.TaskRepository;
import com.example.demo.repositories.task.TaskStatus;
import com.example.demo.repositories.user.Role;
import com.example.demo.repositories.user.UserEntity;
import com.example.demo.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_success_whenAuthorExists() {
        String username = "author";
        CreateTaskRequest request = new CreateTaskRequest("Task title", "Task description", TaskPriority.HIGH, null);

        UserEntity author = user(1L, username, Role.USER);
        TaskEntity taskToSave = TaskEntity.builder()
                .title(request.title())
                .description(request.description())
                .status(TaskStatus.TODO)
                .priority(request.taskPriority())
                .author(author)
                .build();
        TaskEntity savedTask = TaskEntity.builder()
                .id(100L)
                .title(request.title())
                .description(request.description())
                .status(TaskStatus.TODO)
                .priority(request.taskPriority())
                .author(author)
                .createdDate(Instant.now())
                .updatedAt(Instant.now())
                .build();
        TaskResponse expected = new TaskResponse(
                100L,
                request.title(),
                request.description(),
                TaskStatus.TODO,
                request.taskPriority(),
                author.getId(),
                null,
                savedTask.getCreatedDate(),
                savedTask.getUpdatedAt()
        );

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(author));
        when(taskMapper.toEntity(request, author, null)).thenReturn(taskToSave);
        when(taskRepository.save(taskToSave)).thenReturn(savedTask);
        when(taskMapper.toDomain(savedTask)).thenReturn(expected);

        TaskResponse actual = taskService.createTask(request, username);

        assertSame(expected, actual);
        verify(taskRepository).save(taskToSave);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void updateTask_throwsAccessDenied_whenNotAuthorAndNotAdmin() {
        Long taskId = 10L;
        String username = "regular-user";

        UserEntity author = user(1L, "task-author", Role.USER);
        UserEntity currentUser = user(2L, username, Role.USER);
        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .title("Old title")
                .description("Old description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .author(author)
                .build();

        UpdateTaskRequest updateRequest = new UpdateTaskRequest(
                "New title",
                "New description",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                null
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(currentUser));

        assertThrows(
                AccessDeniedException.class,
                () -> taskService.updateTask(taskId, updateRequest, username, false)
        );

        verify(taskRepository, never()).save(task);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void deleteTask_allowsAdmin_evenIfNotAuthor() {
        Long taskId = 25L;
        String username = "admin-user";

        UserEntity author = user(1L, "author", Role.USER);
        UserEntity admin = user(2L, username, Role.ADMIN);
        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .title("Task")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .author(author)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(admin));

        taskService.deleteTask(taskId, username, true);

        verify(taskRepository).delete(eq(task));
    }

    private UserEntity user(Long id, String username, Role role) {
        return UserEntity.builder()
                .id(id)
                .username(username)
                .password("encoded-password")
                .email(username + "@mail.com")
                .role(role)
                .build();
    }
}
