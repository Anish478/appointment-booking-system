package com.appointment.booking_system.service;

import java.time.LocalDate;
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

    public Appointment findById(int id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public Appointment bookAppointment(int appointmentId, String studentEmail) {
        Appointment appointment = findById(appointmentId);
        if (appointment != null && "AVAILABLE".equals(appointment.getStatus())) {
            appointment.setStatus("BOOKED");
            appointment.setStudentEmail(studentEmail);
            return appointmentRepository.save(appointment);
        }
        return null;
    }

    public List<Appointment> createAppointmentSlots(AppointmentScheduleRequest request) {
        List<Appointment> appointments = new ArrayList<>();
        int totalSlotNumber = 1;
        
        // Loop through each day configuration
        for (int i = 0; i < request.getDates().size(); i++) {
            
            // Parse date and times for this day
            LocalDate date = LocalDate.parse(request.getDates().get(i));
            LocalTime dayStartTime = LocalTime.parse(request.getStartTimes().get(i));
            LocalTime dayEndTime = LocalTime.parse(request.getEndTimes().get(i));
            
            LocalTime currentStartTime = dayStartTime;
            
            // Create slots for this date
            while (currentStartTime.isBefore(dayEndTime)) {
                
                LocalTime currentEndTime = currentStartTime.plusMinutes(request.getMeetingDuration());
                
                if (currentEndTime.isAfter(dayEndTime)) {
                    break; 
                }
                
                // Create appointment
                Appointment appointment = new Appointment();
                appointment.setTitle(request.getTitle() + " - Slot " + totalSlotNumber);
                appointment.setAppointmentType(request.getAppointmentType());
                appointment.setDate(date);
                appointment.setStartTime(currentStartTime);
                appointment.setEndTime(currentEndTime);
                appointment.setLocation(request.getLocation());
                appointment.setStatus("AVAILABLE");
                appointment.setStudentEmail(null);
                
                appointments.add(appointmentRepository.save(appointment));
                
                currentStartTime = currentEndTime.plusMinutes(request.getGapBetweenMeetings());
                totalSlotNumber++;
            }
        }
        
        return appointments;
    }

}
