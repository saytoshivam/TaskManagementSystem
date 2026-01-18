package com.indiasatcom.TaskManagementSystem.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Domain entity representing a Task in the system.
 * This is the core domain model following DDD principles.
 */
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Task {
    @Id
    @Column(length = 36)
    private String id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;
    
    @Column(nullable = false)
    private LocalDate dueDate;

    // Custom constructor to handle null status
    public Task(String id, String title, String description, TaskStatus status, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status != null ? status : TaskStatus.PENDING;
        this.dueDate = dueDate;
    }
}

