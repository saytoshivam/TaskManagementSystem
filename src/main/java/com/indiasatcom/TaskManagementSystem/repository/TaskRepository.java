package com.indiasatcom.TaskManagementSystem.repository;

import com.indiasatcom.TaskManagementSystem.domain.Task;
import com.indiasatcom.TaskManagementSystem.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Task persistence operations.
 * Following DDD principles, this interface defines the contract for data access.
 * Uses Spring Data JPA for database operations with H2.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByStatus(TaskStatus status);
    List<Task> findAllByOrderByDueDateAsc();
    
    // Database-level pagination methods
    Page<Task> findByStatusOrderByDueDateAsc(TaskStatus status, Pageable pageable);
    Page<Task> findAllByOrderByDueDateAsc(Pageable pageable);
}

