package com.indiasatcom.TaskManagementSystem.dto;

import com.indiasatcom.TaskManagementSystem.domain.TaskStatus;
import com.indiasatcom.TaskManagementSystem.validation.FutureDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO for Task creation and update requests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private TaskStatus status;
    
    @NotNull(message = "Due date is required")
    @FutureDate(message = "Due date must be a valid date in the future")
    private LocalDate dueDate;
}

