package com.example.demo.controllers.task;

import com.example.demo.dto.task.CreateTaskRequest;
import com.example.demo.dto.task.TaskResponse;
import com.example.demo.dto.task.TasksSearchFilter;
import com.example.demo.dto.task.UpdateTaskRequest;
import com.example.demo.service.task.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest task, Authentication authentication) {

        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(task, username));
    }


    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody UpdateTaskRequest updateTaskRequest, Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return ResponseEntity.ok(taskService.updateTask(id, updateTaskRequest, username, isAdmin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        taskService.deleteTask(id, username, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasksWithFilter(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "assigneeId", required = false) Long assigneeId,
            @RequestParam(value = "authorId", required = false) Long authorId

    ) {
        var filter = new TasksSearchFilter(status, assigneeId, authorId);
        return ResponseEntity.ok(taskService.searchAllTasksByFilter(filter));
    }

}
