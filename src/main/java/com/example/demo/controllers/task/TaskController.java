package com.example.demo.controllers.task;

import com.example.demo.dto.task.CreateTaskRequest;
import com.example.demo.dto.task.TaskResponse;
import com.example.demo.dto.task.TasksSearchFilter;
import com.example.demo.dto.task.UpdateTaskRequest;
import com.example.demo.repositories.task.TaskStatus;
import com.example.demo.service.task.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create task")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Create task without assignee",
                            value = """
                                    {
                                      "title": "Prepare report",
                                      "description": "Q4 report",
                                      "priority": "LOW",
                                      "assigneeId": null
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest task, Authentication authentication) {

        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(task, username));
    }


    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update task")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Update task without assignee",
                            value = """
                                    {
                                      "title": "Prepare report v2",
                                      "description": "Q4 report updated",
                                      "status": "IN_PROGRESS",
                                      "priority": "MEDIUM",
                                      "assigneeId": null
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest updateTaskRequest, Authentication authentication) {
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
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "assigneeId", required = false) @Positive Long assigneeId,
            @RequestParam(value = "authorId", required = false) @Positive Long authorId

    ) {
        var filter = new TasksSearchFilter(status, assigneeId, authorId);
        return ResponseEntity.ok(taskService.searchAllTasksByFilter(filter));
    }

}
