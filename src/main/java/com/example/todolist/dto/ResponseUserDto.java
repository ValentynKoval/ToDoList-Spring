package com.example.todolist.dto;

import lombok.Data;

@Data
public class ResponseUserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean active;
}
