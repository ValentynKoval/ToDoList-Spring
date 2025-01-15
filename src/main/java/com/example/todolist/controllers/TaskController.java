package com.example.todolist.controllers;

import com.example.todolist.dto.TaskDto;
import com.example.todolist.mappers.ResponseTaskMapper;
import com.example.todolist.models.Task;
import com.example.todolist.models.User;
import com.example.todolist.services.TaskService;
import com.example.todolist.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;
    private final ResponseTaskMapper responseTaskMapper;

    @PostMapping()
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskDto taskDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        Task task = taskService.createTask(taskDto, user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email.equals(task.getUser().getEmail()) || task.isShared()) {
            return new ResponseEntity<>(responseTaskMapper.toDto(task), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskById(@PathVariable long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email.equals(task.getUser().getEmail())) {
            taskService.deleteTaskById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping()
    public ResponseEntity<?> getAllTasks() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(taskService.getAllTasks(email), HttpStatus.OK);
    }

    @PutMapping("/{id}/share")
    public ResponseEntity<?> shareTask(@PathVariable long id, HttpServletRequest request) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.equals(task.getUser().getEmail())) {
            return new ResponseEntity<>("You can only share your own tasks", HttpStatus.FORBIDDEN);
        }
        task.setShared(true);
        taskService.updateTask(task);

        String baseUrl = String.format("%s://%s:%d", request.getScheme(), request.getServerName(), request.getServerPort());
        String sharedLink = String.format("%s/api/tasks/%d", baseUrl, task.getId());
        return new ResponseEntity<>(sharedLink, HttpStatus.OK);
    }
}
