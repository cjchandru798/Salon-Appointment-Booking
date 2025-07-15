package com.project.ProjectSalon.serviceimp;

import com.project.ProjectSalon.dto.LoginRequest;
import com.project.ProjectSalon.dto.RegistrationRequest;
import com.project.ProjectSalon.entity.Customers;
import com.project.ProjectSalon.entity.Staff;
import com.project.ProjectSalon.entity.Users;
import com.project.ProjectSalon.repo.CustomerRepo;
import com.project.ProjectSalon.repo.StaffRepo;
import com.project.ProjectSalon.repo.UsersRepo;
import com.project.ProjectSalon.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
public class AuthService {

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private StaffRepo staffRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public String register(RegistrationRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Users.UserRole.CUSTOMER);

        // AUTO-CREATE CUSTOMER or STAFF RECORD
        switch (user.getRole()) {
            case CUSTOMER -> {
                Customers customer = new Customers();
                customer.setUsers(user);
                customer.setFirstName(defaultIfNull(request.getUsername()));
                customer.setLastName(" ");
                customer.setPhone("N/A");
                customer.setAddress("N/A");
                user.setCustomer(customer); // Bidirectional setup (if enabled in entity)
            }
            case STAFF -> {
                Staff staff = new Staff();
                staff.setUsers(user);
                staff.setFirstName(defaultIfNull(request.getUsername()));
                staff.setLastName(" ");
                staff.setPhone("N/A");
                staff.setSpecialization("General");
                user.setStaff(staff); // Bidirectional setup (if enabled in entity)
            }
            case ADMIN -> {
                // No additional entity needed
            }
        }

        userRepo.save(user); // This cascades and saves Customers or Staff if mapped properly
        return jwtUtil.generateToken(user);
    }

    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Users user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtUtil.generateToken(user);
    }

    private String defaultIfNull(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
    }
}
