package com.appointment.booking_system.controller;

import com.appointment.booking_system.dto.LoginRequest;
import com.appointment.booking_system.exception.EmailAlreadyExists;
import com.appointment.booking_system.exception.UserNotFoundException;
import com.appointment.booking_system.exception.WrongPasswordException;
import com.appointment.booking_system.model.User;
import com.appointment.booking_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    

    @PostMapping("/register")
    public User register(@Valid @RequestBody User user) throws EmailAlreadyExists {
       return userService.register(user);
    }
    

    @PostMapping("/login")
    public LoginRequest login(@Valid @RequestBody LoginRequest login) throws UserNotFoundException, WrongPasswordException {
        return userService.login(login);
           
        
    }
}