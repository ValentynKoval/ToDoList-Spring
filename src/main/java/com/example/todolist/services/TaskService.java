package com.example.todolist.services;

import com.example.todolist.dto.ResponseTaskDto;
import com.example.todolist.dto.TaskDto;
import com.example.todolist.mappers.ResponseTaskMapper;
import com.example.todolist.mappers.TaskMapper;
import com.example.todolist.models.Task;
import com.example.todolist.models.User;
import com.example.todolist.repo.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ResponseTaskMapper responseTaskMapper;

    public Task createTask(TaskDto taskDto, User user) {
        Task task = taskMapper.toEntity(taskDto);
        task.setCreateAt(LocalDateTime.now());
        task.setUser(user);
        return taskRepository.save(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }

    public List<ResponseTaskDto> getAllTasks(String email) {
        List<Task> tasks = taskRepository.findAllByUserEmail(email);
        return tasks.stream().map(responseTaskMapper::toDto).toList();
    }

    public void updateTask(Task task) {
        taskRepository.save(task);
    }
}
