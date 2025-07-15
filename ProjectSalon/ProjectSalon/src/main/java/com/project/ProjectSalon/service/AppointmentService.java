package com.project.ProjectSalon.service;

import com.project.ProjectSalon.entity.Appointment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface AppointmentService {

    List<Appointment> getAllAppointment();

    Optional<Appointment> getAppointmentId(Long appointmentId);

    Appointment createAppointment(Appointment appointment);

    void deleteAppointment(Long appointmentId);

    Appointment updateAppointment(Long appointmentId,Appointment appointment);
    List<Appointment> getByCustomer(Long customerId);
    List<Appointment> getByStaff   (Long staffId);
}
