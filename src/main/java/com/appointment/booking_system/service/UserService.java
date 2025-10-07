package com.appointment.booking_system.service;

import com.appointment.booking_system.dto.LoginRequest;
import com.appointment.booking_system.dto.RegisterRequest;
import com.appointment.booking_system.dto.UserResponse;
import com.appointment.booking_system.model.User;
import com.appointment.booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserResponse register(RegisterRequest request) {
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password
        user.setName(request.getName());
        user.setRole(request.getRole());
        
       
        user = userRepository.save(user);
        
        
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole()
        );
    }
    

    public UserResponse login(LoginRequest request) {
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole()
        );
    }
}