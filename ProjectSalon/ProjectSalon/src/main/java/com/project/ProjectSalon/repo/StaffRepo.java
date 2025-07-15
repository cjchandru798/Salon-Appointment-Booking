package com.project.ProjectSalon.repo;

import com.project.ProjectSalon.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepo extends JpaRepository<Staff ,Long> {
    // In StaffRepo.java
    List<Staff> findAllByOrderByStaffIdAsc();

    Optional<Staff> findByUsers_UserId(Long userId);

}
