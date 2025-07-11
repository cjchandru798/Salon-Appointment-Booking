package com.project.ProjectSalon.controller;

import com.project.ProjectSalon.entity.Appointment;
import com.project.ProjectSalon.serviceimp.AppointmentServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    AppointmentServiceImp appointmentServiceImp;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF','CUSTOMER')")
    public ResponseEntity<List<Appointment>> getAllAppointmentOInfo(){
        return ResponseEntity.ok(appointmentServiceImp.getAllAppointment());
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF','CUSTOMER')")
    public ResponseEntity<Optional<Appointment>> getAppointmentIdInfo(@PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentServiceImp.getAppointmentId(appointmentId));
    }
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF','CUSTOMER')")
    public Appointment createAppointmentInfo(@RequestBody Appointment appointment){
        return  appointmentServiceImp.createAppointment(appointment);
    }


    @PutMapping("/update/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF','CUSTOMER')")
    public ResponseEntity<Appointment> updateAppointmentInfo(@PathVariable long appointmentId,@RequestBody Appointment appointment){
        return ResponseEntity.ok(appointmentServiceImp.updateAppointment(appointmentId,appointment));
    }

    @DeleteMapping("/delete/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF','CUSTOMER')")
    public  void deleteAppointmentInfo(@PathVariable long appointmentId){
        appointmentServiceImp.deleteAppointment(appointmentId);
    }

}
