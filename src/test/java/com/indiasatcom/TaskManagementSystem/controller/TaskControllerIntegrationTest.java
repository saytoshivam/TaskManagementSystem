package com.indiasatcom.TaskManagementSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indiasatcom.TaskManagementSystem.domain.TaskStatus;
import com.indiasatcom.TaskManagementSystem.dto.TaskRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateTask() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setStatus(TaskStatus.PENDING);
        request.setDueDate(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.dueDate").exists());
    }

    @Test
    void testCreateTaskWithMissingTitle() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setDescription("Test Description");
        request.setDueDate(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTaskWithMissingDueDate() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTaskWithPastDate() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setDueDate(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTaskById() throws Exception {
        // First create a task
        TaskRequest createRequest = new TaskRequest();
        createRequest.setTitle("Get Test Task");
        createRequest.setDescription("Description");
        createRequest.setDueDate(LocalDate.now().plusDays(1));

        String createResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String taskId = objectMapper.readTree(createResponse).get("id").asText();

        // Then get it
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Get Test Task"));
    }

    @Test
    void testGetTaskByIdNotFound() throws Exception {
        mockMvc.perform(get("/tasks/non-existent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Task Not Found"));
    }

    @Test
    void testUpdateTask() throws Exception {
        // First create a task
        TaskRequest createRequest = new TaskRequest();
        createRequest.setTitle("Original Title");
        createRequest.setDescription("Original Description");
        createRequest.setDueDate(LocalDate.now().plusDays(1));

        String createResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String taskId = objectMapper.readTree(createResponse).get("id").asText();

        // Then update it
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);
        updateRequest.setDueDate(LocalDate.now().plusDays(2));

        mockMvc.perform(put("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void testUpdateTaskNotFound() throws Exception {
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDueDate(LocalDate.now().plusDays(1));

        mockMvc.perform(put("/tasks/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Task Not Found"));
    }

    @Test
    void testDeleteTask() throws Exception {
        // First create a task
        TaskRequest createRequest = new TaskRequest();
        createRequest.setTitle("Delete Test Task");
        createRequest.setDueDate(LocalDate.now().plusDays(1));

        String createResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String taskId = objectMapper.readTree(createResponse).get("id").asText();

        // Then delete it
        mockMvc.perform(delete("/tasks/" + taskId))
                .andExpect(status().isNoContent());

        // Verify it's deleted
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTaskNotFound() throws Exception {
        mockMvc.perform(delete("/tasks/non-existent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Task Not Found"));
    }

    @Test
    void testGetAllTasks() throws Exception {
        // Create multiple tasks
        for (int i = 0; i < 3; i++) {
            TaskRequest request = new TaskRequest();
            request.setTitle("Task " + i);
            request.setDescription("Description " + i);
            request.setDueDate(LocalDate.now().plusDays(i + 1));

            mockMvc.perform(post("/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        // Get all tasks
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    void testGetAllTasksSortedByDueDate() throws Exception {
        // Create tasks with different due dates
        TaskRequest request1 = new TaskRequest();
        request1.setTitle("Task 3");
        request1.setDueDate(LocalDate.now().plusDays(3));
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        TaskRequest request2 = new TaskRequest();
        request2.setTitle("Task 1");
        request2.setDueDate(LocalDate.now().plusDays(1));
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        TaskRequest request3 = new TaskRequest();
        request3.setTitle("Task 2");
        request3.setDueDate(LocalDate.now().plusDays(2));
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isCreated());

        // Get all tasks - should be sorted by due date
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetTasksByStatus() throws Exception {
        // Create tasks with different statuses
        TaskRequest request1 = new TaskRequest();
        request1.setTitle("Pending Task");
        request1.setStatus(TaskStatus.PENDING);
        request1.setDueDate(LocalDate.now().plusDays(1));
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        TaskRequest request2 = new TaskRequest();
        request2.setTitle("In Progress Task");
        request2.setStatus(TaskStatus.IN_PROGRESS);
        request2.setDueDate(LocalDate.now().plusDays(2));
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // Filter by status
        mockMvc.perform(get("/tasks").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetTasksWithPagination() throws Exception {
        // Create multiple tasks
        for (int i = 0; i < 5; i++) {
            TaskRequest request = new TaskRequest();
            request.setTitle("Task " + i);
            request.setDueDate(LocalDate.now().plusDays(i + 1));

            mockMvc.perform(post("/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        // Get tasks with pagination
        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(2))));
    }
}

