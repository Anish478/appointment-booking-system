package com.appointment.booking_system.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appointment.booking_system.dto.AppointmentScheduleRequest;
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

    public List<Appointment> createAppointmentSlots(AppointmentScheduleRequest request) {
        List<Appointment> appointments = new ArrayList<>();
        
        LocalTime currentStartTime = request.getStartTime();
        LocalTime endTimeLimit = request.getEndTime();
        
        int slotNumber = 1;
        
        
        while (currentStartTime.isBefore(endTimeLimit)) {
            
            LocalTime currentEndTime = currentStartTime.plusMinutes(request.getMeetingDuration());
            
            
            if (currentEndTime.isAfter(endTimeLimit)) {
                break; 
            }
            
            
            Appointment appointment = new Appointment();
            appointment.setTitle(request.getTitle() + " - Slot " + slotNumber);
            appointment.setDescription(request.getDescription());
            appointment.setAppointmentType(request.getAppointmentType());
            appointment.setDate(request.getDate());
            appointment.setStartTime(currentStartTime);
            appointment.setEndTime(currentEndTime);
            appointment.setLocation(request.getLocation());
            
            
            appointments.add(appointmentRepository.save(appointment));
            
            
            currentStartTime = currentEndTime.plusMinutes(request.getGapBetweenMeetings());
            slotNumber++;
        }
        
        return appointments;
    }

}
