package com.indiasatcom.TaskManagementSystem.repository;

import com.indiasatcom.TaskManagementSystem.domain.Task;
import com.indiasatcom.TaskManagementSystem.domain.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository repository;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task("1", "Test Task", "Description", TaskStatus.PENDING, LocalDate.now().plusDays(1));
    }

    @Test
    void testSaveAndFindById() {
        repository.save(testTask);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Task> found = repository.findById("1");
        assertTrue(found.isPresent());
        assertEquals("Test Task", found.get().getTitle());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Task> found = repository.findById("non-existent");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        Task task1 = new Task("1", "Task 1", "Desc 1", TaskStatus.PENDING, LocalDate.now().plusDays(1));
        Task task2 = new Task("2", "Task 2", "Desc 2", TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(2));
        repository.save(task1);
        repository.save(task2);
        entityManager.flush();
        entityManager.clear();
        
        List<Task> allTasks = repository.findAll();
        assertEquals(2, allTasks.size());
    }

    @Test
    void testFindByStatus() {
        Task task1 = new Task("1", "Task 1", "Desc 1", TaskStatus.PENDING, LocalDate.now().plusDays(1));
        Task task2 = new Task("2", "Task 2", "Desc 2", TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(2));
        Task task3 = new Task("3", "Task 3", "Desc 3", TaskStatus.PENDING, LocalDate.now().plusDays(3));
        repository.save(task1);
        repository.save(task2);
        repository.save(task3);
        entityManager.flush();
        entityManager.clear();
        
        List<Task> pendingTasks = repository.findByStatus(TaskStatus.PENDING);
        assertEquals(2, pendingTasks.size());
        
        List<Task> inProgressTasks = repository.findByStatus(TaskStatus.IN_PROGRESS);
        assertEquals(1, inProgressTasks.size());
    }

    @Test
    void testDeleteById() {
        repository.save(testTask);
        entityManager.flush();
        
        assertTrue(repository.existsById("1"));
        repository.deleteById("1");
        entityManager.flush();
        assertFalse(repository.existsById("1"));
    }

    @Test
    void testExistsById() {
        repository.save(testTask);
        entityManager.flush();
        
        assertTrue(repository.existsById("1"));
        assertFalse(repository.existsById("2"));
    }

    @Test
    void testFindAllByOrderByDueDateAsc() {
        Task task1 = new Task("1", "Task 1", "Desc 1", TaskStatus.PENDING, LocalDate.now().plusDays(3));
        Task task2 = new Task("2", "Task 2", "Desc 2", TaskStatus.PENDING, LocalDate.now().plusDays(1));
        Task task3 = new Task("3", "Task 3", "Desc 3", TaskStatus.PENDING, LocalDate.now().plusDays(2));
        repository.save(task1);
        repository.save(task2);
        repository.save(task3);
        entityManager.flush();
        entityManager.clear();
        
        List<Task> sortedTasks = repository.findAllByOrderByDueDateAsc();
        
        assertEquals(3, sortedTasks.size());
        assertEquals("Task 2", sortedTasks.get(0).getTitle()); // Earliest due date
        assertEquals("Task 3", sortedTasks.get(1).getTitle());
        assertEquals("Task 1", sortedTasks.get(2).getTitle()); // Latest due date
    }
}
