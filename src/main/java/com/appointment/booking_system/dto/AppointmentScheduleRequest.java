package com.appointment.booking_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentScheduleRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Appointment type is required")
    private String appointmentType; 

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Meeting duration is required")
    @Min(value = 5, message = "Meeting duration must be at least 5 minutes")
    private Integer meetingDuration; 

    @NotNull(message = "Gap between meetings is required")
    @Min(value = 0, message = "Gap cannot be negative")
    private Integer gapBetweenMeetings; 

    @NotBlank(message = "Location is required")
    private String location;
}

