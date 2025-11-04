package com.appointment.booking_system.controller;

import com.appointment.booking_system.dto.LoginRequest;
import com.appointment.booking_system.exception.EmailAlreadyExists;
import com.appointment.booking_system.exception.UserNotFoundException;
import com.appointment.booking_system.exception.WrongPasswordException;
import com.appointment.booking_system.model.User;
import com.appointment.booking_system.service.UserService;
import com.appointment.booking_system.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final UserRepository userRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    

    @PostMapping("/register")
    public User register(@Valid @RequestBody User user) throws EmailAlreadyExists {
       LOGGER.info("Registering user with email: {}", user.getEmail());
       return userService.register(user);
    }
    

    @PostMapping("/login")
    public User login(@Valid @RequestBody LoginRequest loginRequest, HttpSession session) throws UserNotFoundException, WrongPasswordException {
        LOGGER.info("Login attempt for email: {}", loginRequest.getEmail());
        
        
        userService.login(loginRequest);
        
        
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
        if (user.isPresent()) {
            session.setAttribute("user", user.get());
            LOGGER.info("User {} logged in successfully", user.get().getEmail());
            return user.get();
        } else {
            throw new UserNotFoundException("User not found");
        }
    }
}