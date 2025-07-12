package com.project.ProjectSalon.controller;

import com.project.ProjectSalon.entity.Services;
import com.project.ProjectSalon.serviceimp.SalonServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/service")
public class AdminServiceController {

    @Autowired
    private SalonServiceImp salonServiceImp;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Services> createService(@RequestBody Services services) {
        Services created = salonServiceImp.createService(services);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/update/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Services> updateService(
            @PathVariable Long serviceId,
            @RequestBody Services services) {
        Services updated = salonServiceImp.updateService(serviceId, services);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable Long serviceId) {
        salonServiceImp.deleteService(serviceId);
        return ResponseEntity.ok().build();
    }
}
