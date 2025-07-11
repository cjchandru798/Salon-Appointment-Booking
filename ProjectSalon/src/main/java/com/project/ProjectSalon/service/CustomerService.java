package com.project.ProjectSalon.service;

import com.project.ProjectSalon.entity.Customers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CustomerService {

    List<Customers> getALl();

    Optional<Customers> getCustomerId(Long customerId);

    Optional<Customers> getUserByCustomer(Long userId);

//    Optional<Customers> getCustomerPhone(String phone);

    Customers createCustomer(Customers customers);

    Customers updateCustomer(Long customerId,Customers customers);

    void  deleteCustomer(Long customerId);

}
