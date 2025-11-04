package com.appointment.booking_system.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appointment.booking_system.dto.AppointmentScheduleRequest;
import com.appointment.booking_system.model.Appointment;
import com.appointment.booking_system.repository.AppointRepo;
import com.appointment.booking_system.repository.UserRepository;
import com.appointment.booking_system.model.User;

@Service
public class AppointmentService {
    
    @Autowired
    private AppointRepo appointmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    

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
        if (appointment == null || !"AVAILABLE".equals(appointment.getStatus())) {
            return null; 
        }
        

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime slotTime = LocalDateTime.of(
            appointment.getDate(), 
            appointment.getStartTime()
        );
        

        if (currentTime.isAfter(slotTime)) {
            return null; 
        }

        User student = userRepository.findByEmail(studentEmail).orElse(null);
        if (student == null) {
            return null;
        }
        
        String appointmentType = appointment.getAppointmentType();
        
        
        if ("Individual".equals(appointmentType)) {

            List<Appointment> studentBookings = appointmentRepository.findByStudentEmail(studentEmail);
            for (Appointment booked : studentBookings) {
                if ("BOOKED".equals(booked.getStatus()) && "Individual".equals(booked.getAppointmentType())) {
                    return null; 
                }
            }
        }
        
        if ("Group".equals(appointmentType)) {
            Integer studentGroup = student.getGroupNumber();
            if (studentGroup != null) {
                List<User> groupMembers = userRepository.findByGroupNumber(studentGroup);

                for (User member : groupMembers) {
                    List<Appointment> memberBookings = appointmentRepository.findByStudentEmail(member.getEmail());
                    
                    for (Appointment booked : memberBookings) {
                        if ("BOOKED".equals(booked.getStatus()) && "Group".equals(booked.getAppointmentType())) {
                            return null; 
                        }
                    }
                }
            }
        }
        
        appointment.setStatus("BOOKED");
        appointment.setStudentEmail(studentEmail);
        
        if (student.getGroupNumber() != null) {
            appointment.setBookedByGroup(student.getGroupNumber());
        }
        
        return appointmentRepository.save(appointment);
    }

    public Appointment cancelBooking(int appointmentId, String studentEmail) {
        Appointment appointment = findById(appointmentId);
        if (appointment != null && "BOOKED".equals(appointment.getStatus()) 
            && studentEmail.equals(appointment.getStudentEmail())) {
            

            LocalDateTime currentTime = LocalDateTime.now();
            LocalDate appointmentDate = appointment.getDate();


            List<Appointment> allAppointments = appointmentRepository.findAll();
            LocalDateTime earliestSlotTimeOnThatDate = null;
            
            for (Appointment slot : allAppointments) {
               
                if (slot.getDate().isEqual(appointmentDate)) {
                    LocalDateTime slotTime = LocalDateTime.of(
                        slot.getDate(), 
                        slot.getStartTime()
                    );
                    
                   
                    if (earliestSlotTimeOnThatDate == null) {
                        earliestSlotTimeOnThatDate = slotTime;
                    } else if (slotTime.isBefore(earliestSlotTimeOnThatDate)) {
                        earliestSlotTimeOnThatDate = slotTime;
                    }
                }
            }
            
            
            if (earliestSlotTimeOnThatDate != null && currentTime.isAfter(earliestSlotTimeOnThatDate)) {
                return null;
            }
            
            appointment.setStatus("AVAILABLE");
            appointment.setStudentEmail(null);
            appointment.setBookedByGroup(null); 
            return appointmentRepository.save(appointment);
        }
        return null;
    }

    public void deleteAppointment(int appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }

    public List<Appointment> createAppointmentSlots(AppointmentScheduleRequest request) {
        List<Appointment> appointments = new ArrayList<>();
        int totalSlotNumber = 1;
        
        
        for (int i = 0; i < request.getDates().size(); i++) {
            
            
            LocalDate date = LocalDate.parse(request.getDates().get(i));
            LocalTime dayStartTime = LocalTime.parse(request.getStartTimes().get(i));
            LocalTime dayEndTime = LocalTime.parse(request.getEndTimes().get(i));
            
            LocalTime currentStartTime = dayStartTime;
            
            
            while (currentStartTime.isBefore(dayEndTime)) {
                
                LocalTime currentEndTime = currentStartTime.plusMinutes(request.getMeetingDuration());
                
                if (currentEndTime.isAfter(dayEndTime)) {
                    break; 
                }
                
                
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
