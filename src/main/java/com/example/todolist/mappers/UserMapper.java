package com.example.todolist.mappers;

import com.example.todolist.dto.UserDto;
import com.example.todolist.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements EntityMapper<User, UserDto>{
    @Override
    public UserDto toDto(User entity) {
        UserDto userDto = new UserDto();
        userDto.setEmail(entity.getEmail());
        userDto.setPassword(entity.getPassword());
        userDto.setFirstName(entity.getFirstName());
        userDto.setLastName(entity.getLastName());
        userDto.setRole(entity.getRole());
        return userDto;
    }

    @Override
    public User toEntity(UserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRole(dto.getRole());
        return user;
    }
}
