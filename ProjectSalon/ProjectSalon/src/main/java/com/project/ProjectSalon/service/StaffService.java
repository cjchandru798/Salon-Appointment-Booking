// src/main/java/com/project/ProjectSalon/service/StaffService.java
package com.project.ProjectSalon.service;

import com.project.ProjectSalon.dto.StaffSummary;   // <‑‑ ✅ import
import com.project.ProjectSalon.entity.Staff;

import java.util.List;
import java.util.Optional;

public interface StaffService {

    List<Staff> getAll();

    Optional<Staff> getStaffId(Long staffId);

    Optional<Staff> getUserByStaff(Long userId);

    Staff createStaff(Staff staff);

    Staff updateStaff(Long staffId, Staff staff);

    void deleteStaff(Long staffId);

    /* NEW summary endpoint */
    List<StaffSummary> buildSummary();
}
