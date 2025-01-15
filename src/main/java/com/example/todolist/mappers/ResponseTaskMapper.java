package com.example.todolist.mappers;

import com.example.todolist.dto.ResponseTaskDto;
import com.example.todolist.models.Task;
import org.springframework.stereotype.Component;

@Component
public class ResponseTaskMapper implements EntityMapper<Task, ResponseTaskDto> {
    @Override
    public Task toEntity(ResponseTaskDto dto) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setCreateAt(task.getCreateAt());
        task.setDueDate(task.getDueDate());
        task.setComplete(task.isComplete());
        return task;
    }

    @Override
    public ResponseTaskDto toDto(Task entity) {
        ResponseTaskDto dto = new ResponseTaskDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setCreateAt(entity.getCreateAt());
        dto.setDueDate(entity.getDueDate());
        dto.setComplete(entity.isComplete());
        return dto;
    }
}
