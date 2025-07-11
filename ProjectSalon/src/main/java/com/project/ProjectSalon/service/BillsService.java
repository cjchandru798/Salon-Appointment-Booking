package com.project.ProjectSalon.service;

import com.project.ProjectSalon.entity.Bills;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface BillsService {

    List<Bills> getAllBills();

    Optional<Bills> getBillById(Long billId);

    Bills createBill(Bills bills);

    void deleteBill(Long billId);

//    List<Bills> getCustomerBills(Long customerId);
//
//    List<Bills> getBillsByStatus(Bills.PaymentStatus status);
//
//    List<Bills> getBillsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
//
//    BigDecimal calculateRevenue(LocalDateTime startDate, LocalDateTime endDate);



//    Bills updateBill(Long billId, Bills bills);
//
//    Bills processPayment(Long billId, String paymentMethod, String transactionId);
//
//    void deleteBill(Long billId);



}
