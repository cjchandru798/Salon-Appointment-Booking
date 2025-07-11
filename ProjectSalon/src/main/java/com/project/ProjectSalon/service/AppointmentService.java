package com.project.ProjectSalon.service;

import com.project.ProjectSalon.entity.Appointment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AppointmentService {

    List<Appointment> getAllAppointment();

    Optional<Appointment> getAppointmentId(Long appointmentId);

    Appointment createAppointment(Appointment appointment);

    void deleteAppointment(Long appointmentId);

    Appointment updateAppointment(Long appointmentId,Appointment appointment);
}
