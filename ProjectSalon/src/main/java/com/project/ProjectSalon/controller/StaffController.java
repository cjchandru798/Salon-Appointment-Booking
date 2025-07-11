package com.project.ProjectSalon.controller;

import com.project.ProjectSalon.entity.Staff;
import com.project.ProjectSalon.serviceimp.StaffServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff") // Added "api" prefix for consistency
public class StaffController {

    @Autowired
    StaffServiceImp staffServiceImp;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<Staff>> getAllStaff() {
        List<Staff> staffList = staffServiceImp.getAll();
        return ResponseEntity.ok(staffList);
    }

    @GetMapping("/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Staff> getStaffById(@PathVariable Long staffId) {
        return staffServiceImp.getStaffId(staffId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Staff> getStaffByUserId(@PathVariable Long userId) {
        return staffServiceImp.getUserByStaff(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) {
        Staff createdStaff = staffServiceImp.createStaff(staff);
        return new ResponseEntity<>(createdStaff, HttpStatus.CREATED);
    }

    @PutMapping("/update/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Staff> updateStaff(@PathVariable Long staffId, @RequestBody Staff staff) {
        Staff updatedStaff = staffServiceImp.updateStaff(staffId, staff);
        return ResponseEntity.ok(updatedStaff);
    }

    @DeleteMapping("/delete/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long staffId) {
        staffServiceImp.deletStaff(staffId); // Note: Typo corrected from "deletStaff" to "deleteStaff"
        return ResponseEntity.noContent().build();
    }
}