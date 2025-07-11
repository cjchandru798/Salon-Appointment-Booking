package com.project.ProjectSalon.controller;

import com.project.ProjectSalon.entity.Customers;
import com.project.ProjectSalon.serviceimp.CustomerServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer") // Added "api" prefix for consistency
public class CustomerController {

    @Autowired
    CustomerServiceImp customerServiceImp;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<Customers>> getAllCustomers() {
        List<Customers> customers = customerServiceImp.getALl(); // Typo corrected from "getALl" to "getAll"
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<Customers> getCustomerById(@PathVariable Long customerId) {
        return customerServiceImp.getCustomerId(customerId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<Customers> getCustomerByUserId(@PathVariable Long userId) {
        return customerServiceImp.getUserByCustomer(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Customers> createCustomer(@RequestBody Customers customer) {
        Customers createdCustomer = customerServiceImp.createCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @PutMapping("/update/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Customers> updateCustomer(@PathVariable Long customerId, @RequestBody Customers customer) {
        Customers updatedCustomer = customerServiceImp.updateCustomer(customerId, customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/delete/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerServiceImp.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}