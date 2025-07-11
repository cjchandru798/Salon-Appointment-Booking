package com.project.ProjectSalon.repo;

import com.project.ProjectSalon.entity.Bills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillsRepo  extends JpaRepository<Bills ,Long> {

    List<Bills> findByCustomers_CustomerId(Long customerId);
    List<Bills> findByPaymentStatus(Bills.PaymentStatus status);


    @Query("SELECT b FROM Bills b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    List<Bills> findBillsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT SUM(b.amount) FROM Bills b WHERE b.paymentStatus = 'PAID' AND b.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueForPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


}
