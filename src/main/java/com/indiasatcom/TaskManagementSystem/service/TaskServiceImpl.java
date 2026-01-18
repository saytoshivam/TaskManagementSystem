package com.indiasatcom.TaskManagementSystem.service;

import com.indiasatcom.TaskManagementSystem.domain.Task;
import com.indiasatcom.TaskManagementSystem.domain.TaskStatus;
import com.indiasatcom.TaskManagementSystem.exception.TaskNotFoundException;
import com.indiasatcom.TaskManagementSystem.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation for Task business logic.
 * Contains all business rules and orchestrates repository operations.
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public Task createTask(Task task) {
        // Generate unique ID
        task.setId(UUID.randomUUID().toString());
        // Ensure default status is PENDING if not set
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        return taskRepository.save(task);
    }

    @Override
    public Task getTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
    }

    @Override
    public Task updateTask(String id, Task taskUpdates) {
        Task existingTask = getTaskById(id);
        
        // Update only provided fields
        if (taskUpdates.getTitle() != null) {
            existingTask.setTitle(taskUpdates.getTitle());
        }
        if (taskUpdates.getDescription() != null) {
            existingTask.setDescription(taskUpdates.getDescription());
        }
        if (taskUpdates.getStatus() != null) {
            existingTask.setStatus(taskUpdates.getStatus());
        }
        if (taskUpdates.getDueDate() != null) {
            existingTask.setDueDate(taskUpdates.getDueDate());
        }
        
        return taskRepository.save(existingTask);
    }

    @Override
    public void deleteTask(String id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    @Override
    public List<Task> getAllTasksSortedByDueDate() {
        return taskRepository.findAllByOrderByDueDateAsc();
    }

    @Override
    public Page<Task> getAllTasksPaginated(TaskStatus status, Pageable pageable) {
        if (status != null) {
            // Fetch only tasks with the specified status, sorted by due date, with pagination
            return taskRepository.findByStatusOrderByDueDateAsc(status, pageable);
        } else {
            // Fetch all tasks sorted by due date, with pagination
            return taskRepository.findAllByOrderByDueDateAsc(pageable);
        }
    }
}

