package com.example.todolist.mappers;

import com.example.todolist.dto.ResponseUserDto;
import com.example.todolist.models.User;
import org.springframework.stereotype.Component;

@Component
public class ResponseUserMapper implements EntityMapper<User, ResponseUserDto> {
    @Override
    public User toEntity(ResponseUserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setActive(dto.isActive());
        return user;
    }

    @Override
    public ResponseUserDto toDto(User entity) {
        ResponseUserDto dto = new ResponseUserDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setActive(entity.isActive());
        return dto;
    }
}
