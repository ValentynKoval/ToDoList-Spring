package com.example.todolist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private String title;
    private String description;
    private String dueDate;
    private boolean isComplete = false;
    private boolean isShared = false;
}
