package com.project.ProjectSalon.service;

import com.project.ProjectSalon.dto.BillDto;
import com.project.ProjectSalon.entity.Bills;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillsService {

    /* ────── Core Methods ────── */
    List<Bills> getAllBills();
    Optional<Bills> getBillById(Long billId);

    /** @param serviceIds  IDs of services that must be linked to the
     *                     bill’s appointment before the bill is saved. */
    Bills createBill(Bills payload, List<Long> serviceIds);

    Bills updateBill(Long billId, Bills updatedBill);
    Bills processPayment(Long billId, String paymentMethod, String transactionId);
    void deleteBill(Long billId);

    /* ────── Filtered Entities ────── */
    List<Bills> getCustomerBills(Long customerId);
    List<Bills> getBillsByStatus(Bills.PaymentStatus status);
    List<Bills> getBillsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    BigDecimal calculateRevenue(LocalDateTime startDate, LocalDateTime endDate);

    /* ────── DTOs ────── */
    List<BillDto> getAllBillDtos();
    Optional<BillDto> getBillDto(Long billId);
    List<BillDto> getBillDtosByCustomer(Long customerId);
    List<BillDto> getBillDtosByStatus(Bills.PaymentStatus status);
    List<BillDto> getBillDtosByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
