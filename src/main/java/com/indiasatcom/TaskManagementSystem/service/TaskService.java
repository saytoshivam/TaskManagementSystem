package com.indiasatcom.TaskManagementSystem.service;

import com.indiasatcom.TaskManagementSystem.domain.Task;
import com.indiasatcom.TaskManagementSystem.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Task business logic.
 * Following DDD principles, this interface defines the contract for business operations.
 */
public interface TaskService {
    Task createTask(Task task);
    Task getTaskById(String id);
    Task updateTask(String id, Task taskUpdates);
    void deleteTask(String id);
    List<Task> getAllTasks();
    List<Task> getTasksByStatus(TaskStatus status);
    List<Task> getAllTasksSortedByDueDate();
    Page<Task> getAllTasksPaginated(TaskStatus status, Pageable pageable);
}

