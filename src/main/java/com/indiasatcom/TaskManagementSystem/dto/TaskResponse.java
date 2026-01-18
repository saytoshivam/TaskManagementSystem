package com.indiasatcom.TaskManagementSystem.dto;

import com.indiasatcom.TaskManagementSystem.domain.Task;
import com.indiasatcom.TaskManagementSystem.domain.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO for Task response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate()
        );
    }
}

