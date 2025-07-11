package com.project.ProjectSalon.service;

import com.project.ProjectSalon.entity.Staff;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public interface StaffService {

    List<Staff> getAll();

    Optional<Staff> getStaffId(Long staffId);

    Optional<Staff> getUserByStaff(Long userId);

    Staff createStaff(Staff staff);

    Staff updateStaff(Long staffId, Staff staff);

    void deletStaff(Long staffId);

}
