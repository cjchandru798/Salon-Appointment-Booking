package com.project.ProjectSalon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillDto(
        Long billId,
        Long appointmentId,
        String customerName,
        String serviceName,
        BigDecimal amount,
        String paymentStatus,
        LocalDateTime createdAt
) {}
