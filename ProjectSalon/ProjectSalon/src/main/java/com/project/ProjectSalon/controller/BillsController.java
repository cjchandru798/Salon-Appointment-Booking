package com.project.ProjectSalon.controller;

import com.project.ProjectSalon.dto.BillDto;
import com.project.ProjectSalon.entity.Bills;
import com.project.ProjectSalon.serviceimp.BillsServiceImp;
import com.project.ProjectSalon.serviceimp.PDFGeneratorService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Data
class BillRequest {
    private Bills bill;
    private List<Long> serviceIds;
}

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillsController {

    private final BillsServiceImp billsService;
    private final PDFGeneratorService pdfGenerator;

    // ───── READ ─────

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<BillDto>> getAllBills() {
        return ResponseEntity.ok(billsService.getAllBillDtos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<BillDto> getBillById(@PathVariable Long id) {
        return billsService.getBillDto(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        try {
            Optional<Bills> optionalBill = billsService.getBillById(id);
            if (optionalBill.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdfBytes = pdfGenerator.generateBillPDF(optionalBill);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("bill_" + id + ".pdf").build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception ex) {
            ex.printStackTrace();
            // Return a valid ResponseEntity<byte[]> even on error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    // ───── FILTER / SEARCH ─────

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<BillDto>> getBillsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(billsService.getBillDtosByCustomer(customerId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<BillDto>> getBillsByStatus(@PathVariable Bills.PaymentStatus status) {
        return ResponseEntity.ok(billsService.getBillDtosByStatus(status));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<BillDto>> getBillsByDateRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(billsService.getBillDtosByDateRange(start, end));
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<BigDecimal> getRevenue(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(billsService.calculateRevenue(start, end));
    }

    // ───── CREATE ─────

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<BillDto> createBill(@RequestBody BillRequest req) {
        try {
            Bills saved = billsService.createBill(req.getBill(), req.getServiceIds());
            return ResponseEntity.ok(billsService.toDto(saved));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // ───── UPDATE ─────

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<BillDto> updateBill(@PathVariable Long id, @RequestBody Bills updated) {
        try {
            Bills saved = billsService.updateBill(id, updated);
            return ResponseEntity.ok(billsService.toDto(saved));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/pay/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<BillDto> payBill(
            @PathVariable Long id,
            @RequestParam String paymentMethod,
            @RequestParam String transactionId) {
        try {
            Bills paid = billsService.processPayment(id, paymentMethod, transactionId);
            return ResponseEntity.ok(billsService.toDto(paid));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ───── DELETE ─────

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        billsService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }
}
