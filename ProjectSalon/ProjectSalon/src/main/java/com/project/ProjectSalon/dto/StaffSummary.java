// src/main/java/com/project/ProjectSalon/dto/StaffSummary.java
package com.project.ProjectSalon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StaffSummary {

    private Long   staffId;
    private String firstName;
    private String lastName;
    private String phone;
    private String specialization;

    private long   totalAppointments;   // all appointments
    private long   completed;           // completed   (✅)
    private long   scheduled;           // still booked (⏳)
}
