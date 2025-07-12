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
    public Services createService(@RequestBody Services services) {
        return salonServiceImp.createService(services);
    }

    @PutMapping("/update/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Services> updateService(
            @PathVariable Long serviceId,
            @RequestBody Services services) {
        return ResponseEntity.ok(salonServiceImp.updateService(serviceId, services));
    }

    @DeleteMapping("/delete/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId) {
        salonServiceImp.deleteService(serviceId);
        return ResponseEntity.ok().build();
    }
}
