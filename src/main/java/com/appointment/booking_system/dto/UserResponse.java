package com.appointment.booking_system.dto;
import com.appointment.booking_system.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private int id;
    private String name;
    private String email;
    private String role;
}
