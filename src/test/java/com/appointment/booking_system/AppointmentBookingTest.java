package com.appointment.booking_system;

import com.appointment.booking_system.model.Appointment;
import com.appointment.booking_system.model.User;
import com.appointment.booking_system.repository.AppointRepo;
import com.appointment.booking_system.repository.UserRepository;
import com.appointment.booking_system.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AppointmentBookingTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointRepo appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    private User student1;
    private User student2;
    private User student5;
    private Appointment individualSlot;
    private Appointment groupSlot;

    @BeforeEach
    public void setup() {
        appointmentRepository.deleteAll();
        userRepository.deleteAll();

        student1 = new User();
        student1.setName("Test Student 1");
        student1.setEmail("test1@test.com");
        student1.setPassword("pass");
        student1.setRole("STUDENT");
        student1.setGroupNumber(1);
        student1 = userRepository.save(student1);

        student2 = new User();
        student2.setName("Test Student 2");
        student2.setEmail("test2@test.com");
        student2.setPassword("pass");
        student2.setRole("STUDENT");
        student2.setGroupNumber(1);
        student2 = userRepository.save(student2);

        student5 = new User();
        student5.setName("Test Student 5");
        student5.setEmail("test5@test.com");
        student5.setPassword("pass");
        student5.setRole("STUDENT");
        student5.setGroupNumber(2);
        student5 = userRepository.save(student5);

        LocalDate futureDate = LocalDate.now().plusDays(1);
        
        individualSlot = new Appointment();
        individualSlot.setTitle("Individual Slot");
        individualSlot.setAppointmentType("Individual");
        individualSlot.setDate(futureDate);
        individualSlot.setStartTime(LocalTime.of(10, 0));
        individualSlot.setEndTime(LocalTime.of(10, 30));
        individualSlot.setLocation("Room 101");
        individualSlot.setStatus("AVAILABLE");
        individualSlot = appointmentRepository.save(individualSlot);

        groupSlot = new Appointment();
        groupSlot.setTitle("Group Slot");
        groupSlot.setAppointmentType("Group");
        groupSlot.setDate(futureDate);
        groupSlot.setStartTime(LocalTime.of(11, 0));
        groupSlot.setEndTime(LocalTime.of(11, 30));
        groupSlot.setLocation("Room 102");
        groupSlot.setStatus("AVAILABLE");
        groupSlot = appointmentRepository.save(groupSlot);
    }

    @Test
    public void test1_studentCanBookIndividualSlot() {
        Appointment result = appointmentService.bookAppointment(individualSlot.getId(), student1.getEmail());
        
        assertNotNull(result);
        assertEquals("BOOKED", result.getStatus());
        assertEquals(student1.getEmail(), result.getStudentEmail());
    }

    @Test
    public void test2_studentCannotBookTwoIndividualSlots() {
        appointmentService.bookAppointment(individualSlot.getId(), student1.getEmail());

        Appointment secondSlot = new Appointment();
        secondSlot.setTitle("Individual Slot 2");
        secondSlot.setAppointmentType("Individual");
        secondSlot.setDate(LocalDate.now().plusDays(1));
        secondSlot.setStartTime(LocalTime.of(12, 0));
        secondSlot.setEndTime(LocalTime.of(12, 30));
        secondSlot.setLocation("Room 103");
        secondSlot.setStatus("AVAILABLE");
        secondSlot = appointmentRepository.save(secondSlot);

        Appointment result = appointmentService.bookAppointment(secondSlot.getId(), student1.getEmail());
        
        assertNull(result);
    }

    @Test
    public void test3_studentCanBookGroupSlot() {
        Appointment result = appointmentService.bookAppointment(groupSlot.getId(), student1.getEmail());
        
        assertNotNull(result);
        assertEquals("BOOKED", result.getStatus());
        assertEquals(student1.getEmail(), result.getStudentEmail());
        assertEquals(1, result.getBookedByGroup());
    }

    @Test
    public void test4_groupMemberCannotBookSecondGroupSlot() {
        appointmentService.bookAppointment(groupSlot.getId(), student1.getEmail());

        Appointment secondGroupSlot = new Appointment();
        secondGroupSlot.setTitle("Group Slot 2");
        secondGroupSlot.setAppointmentType("Group");
        secondGroupSlot.setDate(LocalDate.now().plusDays(1));
        secondGroupSlot.setStartTime(LocalTime.of(13, 0));
        secondGroupSlot.setEndTime(LocalTime.of(13, 30));
        secondGroupSlot.setLocation("Room 104");
        secondGroupSlot.setStatus("AVAILABLE");
        secondGroupSlot = appointmentRepository.save(secondGroupSlot);

        Appointment result = appointmentService.bookAppointment(secondGroupSlot.getId(), student2.getEmail());
        
        assertNull(result);
    }

    @Test
    public void test5_cannotBookPastSlot() {
        Appointment pastSlot = new Appointment();
        pastSlot.setTitle("Past Slot");
        pastSlot.setAppointmentType("Individual");
        pastSlot.setDate(LocalDate.now().minusDays(1));
        pastSlot.setStartTime(LocalTime.of(10, 0));
        pastSlot.setEndTime(LocalTime.of(10, 30));
        pastSlot.setLocation("Room 105");
        pastSlot.setStatus("AVAILABLE");
        pastSlot = appointmentRepository.save(pastSlot);

        Appointment result = appointmentService.bookAppointment(pastSlot.getId(), student1.getEmail());
        
        assertNull(result);
    }

    @Test
    public void test6_canCancelBeforeEarliestSlotStarts() {
        Appointment futureSlot = new Appointment();
        futureSlot.setTitle("Future Slot");
        futureSlot.setAppointmentType("Individual");
        futureSlot.setDate(LocalDate.now().plusDays(5));
        futureSlot.setStartTime(LocalTime.of(10, 0));
        futureSlot.setEndTime(LocalTime.of(10, 30));
        futureSlot.setLocation("Room 106");
        futureSlot.setStatus("AVAILABLE");
        futureSlot = appointmentRepository.save(futureSlot);

        appointmentService.bookAppointment(futureSlot.getId(), student1.getEmail());
        Appointment result = appointmentService.cancelBooking(futureSlot.getId(), student1.getEmail());
        
        assertNotNull(result);
        assertEquals("AVAILABLE", result.getStatus());
        assertNull(result.getStudentEmail());
    }

    @Test
    public void test7_cannotCancelAfterEarliestSlotStarts() {
        Appointment pastBooking = new Appointment();
        pastBooking.setTitle("Past Booking");
        pastBooking.setAppointmentType("Individual");
        pastBooking.setDate(LocalDate.now().minusDays(1));
        pastBooking.setStartTime(LocalTime.of(10, 0));
        pastBooking.setEndTime(LocalTime.of(10, 30));
        pastBooking.setLocation("Room 107");
        pastBooking.setStatus("BOOKED");
        pastBooking.setStudentEmail(student1.getEmail());
        pastBooking = appointmentRepository.save(pastBooking);

        Appointment result = appointmentService.cancelBooking(pastBooking.getId(), student1.getEmail());
        
        assertNull(result);
    }

    @Test
    public void test8_differentGroupsCanBookSeparateGroupSlots() {
        appointmentService.bookAppointment(groupSlot.getId(), student1.getEmail());

        Appointment anotherGroupSlot = new Appointment();
        anotherGroupSlot.setTitle("Group Slot 3");
        anotherGroupSlot.setAppointmentType("Group");
        anotherGroupSlot.setDate(LocalDate.now().plusDays(1));
        anotherGroupSlot.setStartTime(LocalTime.of(14, 0));
        anotherGroupSlot.setEndTime(LocalTime.of(14, 30));
        anotherGroupSlot.setLocation("Room 108");
        anotherGroupSlot.setStatus("AVAILABLE");
        anotherGroupSlot = appointmentRepository.save(anotherGroupSlot);

        Appointment result = appointmentService.bookAppointment(anotherGroupSlot.getId(), student5.getEmail());
        
        assertNotNull(result);
        assertEquals("BOOKED", result.getStatus());
        assertEquals(2, result.getBookedByGroup());
    }

    @Test
    public void test9_group1MemberCannotBookIfGroup1AlreadyBooked() {
        appointmentService.bookAppointment(groupSlot.getId(), student1.getEmail());

        Appointment result = appointmentService.bookAppointment(groupSlot.getId(), student2.getEmail());
        
        assertNull(result);
    }

    @Test
    public void test10_canDeleteAppointment() {
        appointmentService.deleteAppointment(individualSlot.getId());
        
        Appointment result = appointmentService.findById(individualSlot.getId());
        
        assertNull(result);
    }
}

