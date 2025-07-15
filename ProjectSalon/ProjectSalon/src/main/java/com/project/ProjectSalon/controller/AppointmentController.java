package com.project.ProjectSalon.controller;

import com.project.ProjectSalon.entity.Appointment;
import com.project.ProjectSalon.serviceimp.AppointmentServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST end‑points used by the React front‑end.
 *
 * ─ /all              → admin   (all appointments)
 * ─ /customer/{id}    → customer (appointments for the CUSTOMER that belongs to user id)
 * ─ /staff/{id}       → staff   (appointments for the STAFF member that belongs to user id)
 *
 * NOTE: the {id} carried in the URL is **always the authenticated userId**,
 * not a raw customerId / staffId.  This way the browser only needs one number.
 */
@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentServiceImp service;

    /*──────────────────────── READ ───────────────────────*/

    /** Admin‑only, fetch everything */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Appointment> getAll() {
        return service.getAllAppointment();
    }

    /** Customer dashboard / list */
    @GetMapping("/customer/{userId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Appointment> byCustomer(@PathVariable Long userId) {
        return service.getByCustomer(userId);
    }

    /** Staff dashboard / schedule */
    @GetMapping("/staff/{userId}")
    @PreAuthorize("hasRole('STAFF')")
    public List<Appointment> byStaff(@PathVariable Long userId) {
        return service.getByStaff(userId);
    }

    /** Fetch one appointment by its primary‑key id */
    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','CUSTOMER')")
    public ResponseEntity<Optional<Appointment>> byId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(service.getAppointmentId(appointmentId));
    }

    /*──────────────────────── WRITE ──────────────────────*/

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','CUSTOMER')")
    public Appointment create(@RequestBody Appointment appointment) {
        return service.createAppointment(appointment);
    }

    @PutMapping("/update/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Appointment update(@PathVariable Long appointmentId,
                              @RequestBody Appointment appointment) {
        return service.updateAppointment(appointmentId, appointment);
    }

    @DeleteMapping("/delete/{appointmentId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF')")
    public void delete(@PathVariable Long appointmentId) {
        service.deleteAppointment(appointmentId);
    }

    /** PATCH‑like end‑point to change only the status */
    @PutMapping("/{appointmentId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Void> updateStatus(@PathVariable Long appointmentId,
                                             @RequestBody Map<String,String> body) {
        service.updateStatus(appointmentId, body.get("status"));   // expects {"status":"COMPLETED"}
        return ResponseEntity.ok().build();
    }
}
