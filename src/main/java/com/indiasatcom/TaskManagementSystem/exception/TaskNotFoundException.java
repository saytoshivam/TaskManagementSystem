package com.indiasatcom.TaskManagementSystem.exception;

/**
 * Exception thrown when a task is not found.
 */
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}

