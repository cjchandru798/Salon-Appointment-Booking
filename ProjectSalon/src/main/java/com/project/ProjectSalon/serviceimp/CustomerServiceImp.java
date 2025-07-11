package com.project.ProjectSalon.serviceimp;

import com.project.ProjectSalon.entity.Customers;
import com.project.ProjectSalon.entity.Users;
import com.project.ProjectSalon.repo.CustomerRepo;
import com.project.ProjectSalon.repo.UsersRepo;
import com.project.ProjectSalon.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CustomerServiceImp implements CustomerService {

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    UsersRepo usersRepo;

    @Override
    public List<Customers> getALl() {
        return customerRepo.findAll();
    }

    @Override
    public Optional<Customers> getCustomerId(Long customerId) {
        return customerRepo.findByCustomerId(customerId);
    }

    @Override
    public Optional<Customers> getUserByCustomer(Long userId) {
        return customerRepo.findByUsers_UserId(userId);
    }

//    @Override
//    public Optional<Customers> getCustomerPhone(String phone) {
//        return customerRepo.findByPhone(phone);
//    }

    @Override
    public Customers createCustomer(Customers customers) {
        if(customers.getUsers() == null || customers.getUsers().getUserId() == null){
            throw new IllegalArgumentException("User must be provided for Customer");
        }
         Users user =usersRepo.findById(customers.getUsers().getUserId())
                 .orElseThrow(()->new RuntimeException("User not found"));
        customers.setUsers(user);
        return customerRepo.save(customers);
    }

    @Override
    public Customers updateCustomer(Long customerId, Customers customers) {
        Customers updateCus = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if(customers.getUsers() != null && customers.getUsers().getUserId() != null){
            Users users =usersRepo.findById(customers.getUsers().getUserId())
                    .orElseThrow(()-> new RuntimeException("Nor FOund"));
            updateCus.setUsers(users);
        }

        updateCus.setFirstName(customers.getFirstName());
        updateCus.setLastName(customers.getLastName());
        updateCus.setPhone(customers.getPhone());
        updateCus.setAddress(customers.getAddress());

        return customerRepo.save(updateCus);
    }



    @Override
    public void deleteCustomer(Long customerId) {
        customerRepo.deleteById(customerId);
    }
}
