package com.project.ProjectSalon.controller;

import com.project.ProjectSalon.entity.Services;
import com.project.ProjectSalon.serviceimp.SalonServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salon/service")
public class SalonServiceController {

    @Autowired
    SalonServiceImp salonServiceImp;

    @GetMapping("/all")
    public List<Services> getAllServices(){
        return salonServiceImp.getAllServices();
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<Services> getServiceById(@PathVariable Long serviceId) {
        return salonServiceImp.getServiceById(serviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Services createService(@RequestBody Services services) {

        return salonServiceImp.createService(services);
    }

    @PutMapping("/update/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Services> updateService(@PathVariable Long serviceId, @RequestBody Services services) {
        return ResponseEntity.ok(salonServiceImp.updateService(serviceId, services));
    }

    @DeleteMapping("/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId) {
        salonServiceImp.deleteService(serviceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/category/{category}")
    public List<Services> getServicesByCategory(@PathVariable String category) {
        return salonServiceImp.getServicesByCategory(category);
    }

}
