package com.project.ProjectSalon.serviceimp;
import com.project.ProjectSalon.entity.Appointment;
import com.project.ProjectSalon.entity.Bills;
import com.project.ProjectSalon.entity.Customers;
import com.project.ProjectSalon.repo.AppointmentRepository;
import com.project.ProjectSalon.repo.BillsRepo;
import com.project.ProjectSalon.repo.CustomerRepo;
import com.project.ProjectSalon.service.BillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class BillsServiceImp implements BillsService {

    @Autowired
    BillsRepo billsRepo;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    CustomerRepo customerRepo;

    @Override
    public List<Bills> getAllBills() {
        return billsRepo.findAll() ;
    }

    @Override
    public Optional<Bills> getBillById(Long billId) {
        return billsRepo.findById(billId);
    }

//    @Override
//    public List<Bills> getCustomerBills(Long customerId) {
//        return billsRepo.findByCustomers_CustomerId(customerId);
//    }
//
//    @Override
//    public List<Bills> getBillsByStatus(Bills.PaymentStatus status) {
//        return billsRepo.findByPaymentStatus(status);
//    }
//
//    @Override
//    public List<Bills> getBillsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
//        return billsRepo.findBillsByDateRange(startDate,endDate);
//    }
//
//    @Override
//    public BigDecimal calculateRevenue(LocalDateTime startDate, LocalDateTime endDate) {
//        return billsRepo.calculateRevenueForPeriod(startDate,endDate);
//    }

    @Override
    public Bills createBill(Bills bills) {
        Appointment appointment =appointmentRepository.findById(bills.getAppointment().getAppointmentId())
                .orElseThrow(()-> new RuntimeException("Not found"));

        Customers customers = customerRepo.findById(bills.getCustomers().getCustomerId())
                .orElseThrow(()->new RuntimeException("Not found"));

        Bills newBill = new Bills();
        newBill.setAppointment(appointment);
        newBill.setCustomers(customers);
        newBill.setAmount(bills.getAmount());
        newBill.setPaymentMethod(bills.getPaymentMethod());
        newBill.setTransactionId(bills.getTransactionId());
        newBill.setPaymentStatus(bills.getPaymentStatus());

        if (bills.getCreatedAt() == null) {
            newBill.setCreatedAt(LocalDateTime.now());
        } else {
            newBill.setCreatedAt(bills.getCreatedAt());
        }

        return billsRepo.save(newBill);
    }

    @Override
    public void deleteBill(Long billId) {
        billsRepo.deleteById(billId);
    }

//    @Override
//    public Bills updateBill(Long billId, Bills bills) {
//        Bills update = billsRepo.findById(billId).orElseThrow(()-> new RuntimeException("Not found"));
//
//        update.setAmount(bills.getAmount());
//        update.setPaymentStatus(bills.getPaymentStatus());
//        update.setPaymentMethod(bills.getPaymentMethod());
//        update.setTransactionId(bills.getTransactionId());
//        return billsRepo.save(bills);
//    }

//    @Override
//    public Bills processPayment(Long billId, String paymentMethod, String transactionId) {
//        Bills bill = billsRepo.findById(billId).orElseThrow(()-> new RuntimeException("Not found"));
//
//        bill.setPaymentStatus(Bills.PaymentStatus.PAID);
//        bill.setPaymentMethod(paymentMethod);
//        bill.setTransactionId(transactionId);
//
//        return billsRepo.save(bill);
//    }
//
//    @Override
//    public void deleteBill(Long billId) {
//        billsRepo.deleteById(billId);
//    }
}
