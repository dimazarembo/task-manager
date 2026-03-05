package com.example.demo.service.task;

import com.example.demo.dto.task.CreateTaskRequest;
import com.example.demo.dto.task.TaskMapper;
import com.example.demo.dto.task.TaskResponse;
import com.example.demo.repositories.task.TaskRepository;
import com.example.demo.repositories.user.UserEntity;
import com.example.demo.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    @Transactional
    public TaskResponse createTask(CreateTaskRequest taskRequest, String username) {

        UserEntity author = userRepository.findByUsername(username).orElseThrow();

        UserEntity assignee = null;
        if(taskRequest.assigneeId()!= null){
            assignee = userRepository.findById(taskRequest.assigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
        }

        var taskToSave = taskMapper.toEntity(taskRequest, author, assignee);
        var savedTask = taskRepository.save(taskToSave);
        return taskMapper.toDomain(savedTask);
    }
}
