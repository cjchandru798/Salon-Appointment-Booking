package com.project.ProjectSalon.controller;

import com.project.ProjectSalon.entity.Services;
import com.project.ProjectSalon.serviceimp.SalonServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salon/service")
public class PublicServiceController {

    @Autowired
    private SalonServiceImp salonServiceImp;

    @GetMapping("/all")
    public ResponseEntity<List<Services>> getAllServices() {
        return ResponseEntity.ok(salonServiceImp.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Services> getServiceById(@PathVariable Long id) {
        return salonServiceImp.getServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Services>> getServicesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(salonServiceImp.getServicesByCategory(category));
    }
}
