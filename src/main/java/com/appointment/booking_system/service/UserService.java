package com.appointment.booking_system.service;

import com.appointment.booking_system.dto.LoginRequest;
import com.appointment.booking_system.exception.EmailAlreadyExists;
import com.appointment.booking_system.exception.UserNotFoundException;
import com.appointment.booking_system.exception.WrongPasswordException;
import com.appointment.booking_system.model.User;
import com.appointment.booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(User user) throws EmailAlreadyExists {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExists("Email already Exists!");
        }

        

        return  userRepository.save(user);

        
    }

    public LoginRequest login(LoginRequest login)  throws UserNotFoundException, WrongPasswordException{

        Optional<User> user = userRepository.findByEmail(login.getEmail());
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + login.getEmail());
        }

        if (!login.getPassword().equals(user.get().getPassword())) {
            throw new WrongPasswordException("Invalid email or password");
        }

        return login;
    }
}
