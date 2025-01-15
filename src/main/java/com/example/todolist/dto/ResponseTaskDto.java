package com.example.todolist.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ResponseTaskDto {
    private Long id;

    private String title;

    private String description;

    private LocalDateTime createAt;

    private LocalDateTime dueDate;

    private boolean isComplete;
}
