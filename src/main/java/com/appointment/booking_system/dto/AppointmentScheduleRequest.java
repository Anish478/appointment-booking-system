package com.appointment.booking_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentScheduleRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Appointment type is required")
    private String appointmentType; 

    @NotNull(message = "Meeting duration is required")
    @Min(value = 5, message = "Meeting duration must be at least 5 minutes")
    private Integer meetingDuration; 

    @NotNull(message = "Gap between meetings is required")
    @Min(value = 0, message = "Gap cannot be negative")
    private Integer gapBetweenMeetings; 

    @NotBlank(message = "Location is required")
    private String location;

    // Lists to hold multiple days
    private List<String> dates;        // Will be sent as strings from form
    private List<String> startTimes;   // Will be sent as strings from form
    private List<String> endTimes;     // Will be sent as strings from form
}

