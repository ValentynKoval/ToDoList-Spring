package com.example.todolist.mappers;

public interface EntityMapper<E, D>{
    E toEntity(D dto);
    D toDto(E entity);
}
