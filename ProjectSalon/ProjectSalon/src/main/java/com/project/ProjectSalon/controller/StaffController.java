package com.project.ProjectSalon.controller;

import com.project.ProjectSalon.dto.StaffSummary;
import com.project.ProjectSalon.entity.Staff;
import com.project.ProjectSalon.serviceimp.StaffServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffServiceImp staffService;

    /**
     * ✅ Returns staff summary for dashboard view
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StaffSummary>> getStaffSummary() {
        return ResponseEntity.ok(staffService.buildSummary());
    }

    /**
     * ✅ All staff full list (fallback)
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<Staff>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAll());
    }

    /**
     * ✅ Single staff by ID
     */
    @GetMapping("/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Staff> getStaffById(@PathVariable Long staffId) {
        return staffService.getStaffId(staffId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ✅ Get staff by UserId
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Staff> getStaffByUserId(@PathVariable Long userId) {
        return staffService.getUserByStaff(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ✅ Admin: create staff
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) {
        return new ResponseEntity<>(staffService.createStaff(staff), HttpStatus.CREATED);
    }

    /**
     * ✅ Admin or staff update
     */
    @PutMapping("/update/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Staff> updateStaff(@PathVariable Long staffId,
                                             @RequestBody Staff staff) {
        return ResponseEntity.ok(staffService.updateStaff(staffId, staff));
    }

    /**
     * ✅ Admin delete
     */
    @DeleteMapping("/delete/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long staffId) {
        staffService.deleteStaff(staffId); // ✅ fixed typo from deletStaff to deleteStaff
        return ResponseEntity.noContent().build();
    }
}
