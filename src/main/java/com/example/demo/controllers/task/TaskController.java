package com.example.demo.controllers.task;

import com.example.demo.dto.task.CreateTaskRequest;
import com.example.demo.dto.task.TaskResponse;
import com.example.demo.service.task.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask (@RequestBody CreateTaskRequest task, Authentication authentication) {

        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(task, username));
    }

}
