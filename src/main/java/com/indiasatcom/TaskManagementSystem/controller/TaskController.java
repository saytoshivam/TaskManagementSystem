package com.indiasatcom.TaskManagementSystem.controller;

import com.indiasatcom.TaskManagementSystem.domain.Task;
import com.indiasatcom.TaskManagementSystem.domain.TaskStatus;
import com.indiasatcom.TaskManagementSystem.dto.TaskRequest;
import com.indiasatcom.TaskManagementSystem.dto.TaskResponse;
import com.indiasatcom.TaskManagementSystem.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Task Management API.
 * Handles all HTTP requests related to tasks.
 */
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    /**
     * Create a new task.
     * POST /tasks
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
        
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(TaskResponse.from(createdTask), HttpStatus.CREATED);
    }

    /**
     * Get a task by ID.
     * GET /tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(TaskResponse.from(task));
    }

    /**
     * Update a task.
     * PUT /tasks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequest request) {
        Task taskUpdates = new Task();
        taskUpdates.setTitle(request.getTitle());
        taskUpdates.setDescription(request.getDescription());
        taskUpdates.setStatus(request.getStatus());
        taskUpdates.setDueDate(request.getDueDate());
        
        Task updatedTask = taskService.updateTask(id, taskUpdates);
        return ResponseEntity.ok(TaskResponse.from(updatedTask));
    }

    /**
     * Delete a task.
     * DELETE /tasks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * List all tasks with optional filtering and pagination.
     * GET /tasks?status=PENDING&page=0&size=10
     * Uses database-level pagination for efficient querying.
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        // Create Pageable for database-level pagination
        Pageable pageable = PageRequest.of(page, size);
        
        // Fetch only the required page from database (with optional status filter)
        Page<Task> taskPage = taskService.getAllTasksPaginated(status, pageable);
        
        // Convert to response DTOs
        List<TaskResponse> responses = taskPage.getContent().stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
}

