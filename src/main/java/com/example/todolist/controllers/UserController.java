package com.example.todolist.controllers;

import com.example.todolist.dto.AuthUserDto;
import com.example.todolist.dto.UserDto;
import com.example.todolist.models.User;
import com.example.todolist.services.JwtService;
import com.example.todolist.services.MailSenderService;
import com.example.todolist.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MailSenderService mailSenderService;

    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto) {
        try {
            User user = userService.createNewUser(userDto);
            String message = "Thank you for registering for our service! It will be very useful for you in planning your day and assigning tasks.";
            mailSenderService.send(user.getEmail(), "Registration", message);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody AuthUserDto authUserDto, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authUserDto.getEmail(), authUserDto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.getUserByEmail(authUserDto.getEmail());

            Map<String, String> tokens = jwtService.getTokens(user);

            Cookie refreshCookie = new Cookie("refresh_token", tokens.get("refresh_token"));
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(refreshCookie);

            return ResponseEntity.ok(new HashMap<>() {{put("access_token", tokens.get("access_token"));}});
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
            catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue("refresh_token") String refreshToken) {
        try {
            if (refreshToken == null) {
                throw new Exception("Refresh token is null");
            }
            String accessToken = jwtService.refreshToken(refreshToken);
            return ResponseEntity.ok(new HashMap<>() {{put("access_token", accessToken);}});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("refresh_token") String refreshToken, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        if (refreshToken != null) {
            jwtService.revokeToken(refreshToken);
            Cookie cookie = new Cookie("refresh_token", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getUserDtoByEmail(email));
    }
}
