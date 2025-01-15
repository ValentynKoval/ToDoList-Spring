package com.example.todolist.services;

import com.example.todolist.dto.ResponseUserDto;
import com.example.todolist.dto.UserDto;
import com.example.todolist.mappers.ResponseUserMapper;
import com.example.todolist.mappers.UserMapper;
import com.example.todolist.models.Role;
import com.example.todolist.models.User;
import com.example.todolist.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ResponseUserMapper responseUserMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), user.getAuthorities()
        );
    }

    public User createNewUser(UserDto userDto) {
        if (!userDto.passwordsMatch()) {
            throw new IllegalArgumentException("Passwords do not match!");
        }
        User user = userMapper.toEntity(userDto);
        user.setActive(true);
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public ResponseUserDto getUserDtoByEmail(String email) {
        return responseUserMapper.toDto(getUserByEmail(email));
    }
}
