package com.project.ProjectSalon.repo;

import com.project.ProjectSalon.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customers ,Long> {

    Optional<Customers> findByCustomerId(Long customerId);
    Optional<Customers> findByUsers_UserId(Long userId);
    Optional<Customers> findByPhone(String phone);
}
