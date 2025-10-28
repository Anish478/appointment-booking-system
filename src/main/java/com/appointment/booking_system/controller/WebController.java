package com.appointment.booking_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.Errors;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import com.appointment.booking_system.dto.AppointmentScheduleRequest;
import com.appointment.booking_system.model.User;
import com.appointment.booking_system.model.Appointment;
import com.appointment.booking_system.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final AppointmentService appointmentService;


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "home";
    }
    
    @GetMapping("/appointments/book")
    public String viewAppointments(Model model, HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Appointment> appointments = appointmentService.findAll();
        model.addAttribute("appointments", appointments);
        model.addAttribute("user", user);
        return "view-appointments-student";
    }
    
    @GetMapping("/appointments/schedule")
    public String createAppointment(Model model, HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        
        if (!"PROFESSOR".equals(user.getRole())) {
            return "redirect:/";
        }
        
        model.addAttribute("user", user);
        return "create-appointment-prof";
    }
    
    @PostMapping("/appointments")
    public String processAppointment(AppointmentScheduleRequest scheduleRequest, 
                                     HttpSession session,
                                     Model model) {
        User user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        
        if (!"PROFESSOR".equals(user.getRole())) {
            return "redirect:/";
        }
        
        // Create multiple appointment slots based on the schedule request
        List<Appointment> createdAppointments = appointmentService.createAppointmentSlots(scheduleRequest);
        
        model.addAttribute("successMessage", 
            "Successfully created " + createdAppointments.size() + " appointment slot(s)!");
        
        return "redirect:/appointments/upcoming";
    }
    
    @GetMapping("/appointments/upcoming")
    public String viewUpcomingAppointments(Model model, HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Appointment> appointments = appointmentService.findAll();
        model.addAttribute("appointments", appointments);
        model.addAttribute("user", user);
        return "view-appointments-prof";
    }
    
    @PostMapping("/appointments/{id}/book")
    public String bookAppointment(@PathVariable int id, HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        
        appointmentService.bookAppointment(id, user.getEmail());
        return "redirect:/appointments/book";
    }
    
    @PostMapping("/appointments/{id}/cancel")
    public String cancelBooking(@PathVariable int id, HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        
        appointmentService.cancelBooking(id, user.getEmail());
        return "redirect:/appointments/book";
    }
    
    @PostMapping("/appointments/{id}/delete")
    public String deleteAppointment(@PathVariable int id, HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        
        if (!"PROFESSOR".equals(user.getRole())) {
            return "redirect:/";
        }
        
        appointmentService.deleteAppointment(id);
        return "redirect:/appointments/upcoming";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}