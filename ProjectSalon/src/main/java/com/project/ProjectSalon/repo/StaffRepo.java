package com.project.ProjectSalon.repo;

import com.project.ProjectSalon.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepo extends JpaRepository<Staff ,Long> {

    Optional<Staff> findByUsers_UserId(Long userId);

}
