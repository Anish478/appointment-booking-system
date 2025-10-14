package com.appointment.booking_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appointment.booking_system.model.Appointment;
import com.appointment.booking_system.repository.AppointRepo;

@Service
public class AppointmentService {
    
   


    @Autowired
    private AppointRepo appointmentRepository;
    

    public Appointment createAppointment(Appointment a) {
        return appointmentRepository.save(a);
    }


   
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }
    

}
