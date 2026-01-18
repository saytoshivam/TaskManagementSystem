package com.indiasatcom.TaskManagementSystem.service;

import com.indiasatcom.TaskManagementSystem.domain.Task;
import com.indiasatcom.TaskManagementSystem.domain.TaskStatus;
import com.indiasatcom.TaskManagementSystem.exception.TaskNotFoundException;
import com.indiasatcom.TaskManagementSystem.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepository;
    
    @InjectMocks
    private TaskServiceImpl taskService;
    
    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task("1", "Test Task", "Description", TaskStatus.PENDING, LocalDate.now().plusDays(1));
    }

    @Test
    void testCreateTask() {
        Task newTask = new Task(null, "New Task", "New Description", null, LocalDate.now().plusDays(1));
        Task savedTask = new Task("generated-id", "New Task", "New Description", TaskStatus.PENDING, LocalDate.now().plusDays(1));
        
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId("generated-id");
            return task;
        });
        
        Task result = taskService.createTask(newTask);
        
        assertNotNull(result.getId());
        assertEquals(TaskStatus.PENDING, result.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testGetTaskById() {
        when(taskRepository.findById("1")).thenReturn(Optional.of(testTask));
        
        Task result = taskService.getTaskById("1");
        
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).findById("1");
    }

    @Test
    void testGetTaskByIdNotFound() {
        when(taskRepository.findById("non-existent")).thenReturn(Optional.empty());
        
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById("non-existent"));
        verify(taskRepository, times(1)).findById("non-existent");
    }

    @Test
    void testUpdateTask() {
        Task updatedTask = new Task("1", "Updated Title", "Updated Description", TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(2));
        
        when(taskRepository.findById("1")).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        
        Task taskUpdates = new Task();
        taskUpdates.setTitle("Updated Title");
        taskUpdates.setDescription("Updated Description");
        taskUpdates.setStatus(TaskStatus.IN_PROGRESS);
        taskUpdates.setDueDate(LocalDate.now().plusDays(2));
        
        Task result = taskService.updateTask("1", taskUpdates);
        
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        verify(taskRepository, times(1)).findById("1");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTaskPartial() {
        when(taskRepository.findById("1")).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Task taskUpdates = new Task();
        taskUpdates.setTitle("Only Title Updated");
        
        Task result = taskService.updateTask("1", taskUpdates);
        
        assertEquals("Only Title Updated", result.getTitle());
        assertEquals("Description", result.getDescription()); // Original value preserved
        verify(taskRepository, times(1)).findById("1");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTaskNotFound() {
        when(taskRepository.findById("non-existent")).thenReturn(Optional.empty());
        
        Task taskUpdates = new Task();
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask("non-existent", taskUpdates));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testDeleteTask() {
        when(taskRepository.existsById("1")).thenReturn(true);
        doNothing().when(taskRepository).deleteById("1");
        
        taskService.deleteTask("1");
        
        verify(taskRepository, times(1)).existsById("1");
        verify(taskRepository, times(1)).deleteById("1");
    }

    @Test
    void testDeleteTaskNotFound() {
        when(taskRepository.existsById("non-existent")).thenReturn(false);
        
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask("non-existent"));
        verify(taskRepository, never()).deleteById(anyString());
    }

    @Test
    void testGetAllTasks() {
        List<Task> tasks = Arrays.asList(testTask, new Task("2", "Task 2", "Desc 2", TaskStatus.DONE, LocalDate.now().plusDays(2)));
        when(taskRepository.findAll()).thenReturn(tasks);
        
        List<Task> result = taskService.getAllTasks();
        
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testGetTasksByStatus() {
        List<Task> pendingTasks = Arrays.asList(testTask);
        when(taskRepository.findByStatus(TaskStatus.PENDING)).thenReturn(pendingTasks);
        
        List<Task> result = taskService.getTasksByStatus(TaskStatus.PENDING);
        
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByStatus(TaskStatus.PENDING);
    }

    @Test
    void testGetAllTasksSortedByDueDate() {
        Task task1 = new Task("1", "Task 1", "Desc 1", TaskStatus.PENDING, LocalDate.now().plusDays(3));
        Task task2 = new Task("2", "Task 2", "Desc 2", TaskStatus.PENDING, LocalDate.now().plusDays(1));
        Task task3 = new Task("3", "Task 3", "Desc 3", TaskStatus.PENDING, LocalDate.now().plusDays(2));
        List<Task> tasks = Arrays.asList(task2, task3, task1); // Already sorted by due date
        
        when(taskRepository.findAllByOrderByDueDateAsc()).thenReturn(tasks);
        
        List<Task> result = taskService.getAllTasksSortedByDueDate();
        
        assertEquals(3, result.size());
        assertEquals("Task 2", result.get(0).getTitle()); // Earliest due date
        assertEquals("Task 3", result.get(1).getTitle());
        assertEquals("Task 1", result.get(2).getTitle()); // Latest due date
        verify(taskRepository, times(1)).findAllByOrderByDueDateAsc();
    }
}

