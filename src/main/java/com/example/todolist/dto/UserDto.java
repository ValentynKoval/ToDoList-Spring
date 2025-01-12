package com.example.todolist.dto;

import com.example.todolist.models.Role;
import lombok.Data;

@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}
