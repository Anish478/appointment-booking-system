package com.appointment.booking_system.repository;



import com.appointment.booking_system.model.Appointment;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointRepo extends JpaRepository<Appointment, Integer> {
    Optional<Appointment> findById(int id);
    

    
}