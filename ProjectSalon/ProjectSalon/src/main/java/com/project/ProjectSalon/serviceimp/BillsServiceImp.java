package com.project.ProjectSalon.serviceimp;

import com.project.ProjectSalon.dto.BillDto;
import com.project.ProjectSalon.entity.*;
import com.project.ProjectSalon.repo.*;
import com.project.ProjectSalon.service.BillsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BillsServiceImp implements BillsService {

    private final BillsRepo             billsRepo;
    private final AppointmentRepository appointmentRepo;
    private final CustomerRepo          customerRepo;
    private final ServiceRepo           serviceRepo;

    public BillsServiceImp(BillsRepo billsRepo,
                           AppointmentRepository appointmentRepo,
                           CustomerRepo customerRepo,
                           ServiceRepo serviceRepo) {
        this.billsRepo       = billsRepo;
        this.appointmentRepo = appointmentRepo;
        this.customerRepo    = customerRepo;
        this.serviceRepo     = serviceRepo;
    }

    @Override
    public Bills createBill(Bills payload, List<Long> serviceIds) {

        Appointment appt = appointmentRepo.findById(
                        payload.getAppointment().getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Customers cust = customerRepo.findById(
                        payload.getCustomers().getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // ✅ Resolve services and attach to appointment
        if (serviceIds == null || serviceIds.isEmpty()) {
            throw new RuntimeException("At least one service is required");
        }

        List<Services> services = serviceRepo.findAllById(serviceIds);
        if (services.size() != serviceIds.size()) {
            throw new RuntimeException("Some service IDs are invalid");
        }

        appt.setServices(services);
        appointmentRepo.save(appt);

        Bills b = new Bills();
        b.setAppointment  (appt);
        b.setCustomers    (cust);
        b.setAmount       (payload.getAmount());
        b.setPaymentMethod(payload.getPaymentMethod());
        b.setTransactionId(payload.getTransactionId());
        b.setPaymentStatus(payload.getPaymentStatus());
        b.setCreatedAt(payload.getCreatedAt() != null
                ? payload.getCreatedAt()
                : LocalDateTime.now());

        return billsRepo.save(b);
    }

    // Existing methods unchanged...

    @Override public List<Bills> getAllBills() { return billsRepo.findAll(); }
    @Override public Optional<Bills> getBillById(Long id) { return billsRepo.findById(id); }
    @Override public Bills updateBill(Long id, Bills upd) {
        Bills b = billsRepo.findById(id).orElseThrow(() -> new RuntimeException("Bill not found"));
        b.setAmount        (upd.getAmount());
        b.setPaymentMethod (upd.getPaymentMethod());
        b.setTransactionId (upd.getTransactionId());
        b.setPaymentStatus (upd.getPaymentStatus());
        return billsRepo.save(b);
    }
    @Override public Bills processPayment(Long id, String method, String txn) {
        Bills b = billsRepo.findById(id).orElseThrow(() -> new RuntimeException("Bill not found"));
        b.setPaymentStatus(Bills.PaymentStatus.PAID);
        b.setPaymentMethod(method);
        b.setTransactionId(txn);
        return billsRepo.save(b);
    }
    @Override public void deleteBill(Long id) { billsRepo.deleteById(id); }
    @Override public List<Bills> getCustomerBills(Long cid) { return billsRepo.findByCustomers_CustomerId(cid); }
    @Override public List<Bills> getBillsByStatus(Bills.PaymentStatus s) { return billsRepo.findByPaymentStatus(s); }
    @Override public List<Bills> getBillsByDateRange(LocalDateTime from, LocalDateTime to) { return billsRepo.findBillsByDateRange(from, to); }
    @Override public BigDecimal calculateRevenue(LocalDateTime from, LocalDateTime to) { return billsRepo.calculateRevenueForPeriod(from, to); }

    public BillDto toDto(Bills b) {
        String custName = "Unknown";
        if (b.getCustomers() != null) {
            String fn = b.getCustomers().getFirstName();
            String ln = b.getCustomers().getLastName();
            custName = ((fn != null ? fn : "") + " " + (ln != null ? ln : "")).trim();
        }

        String svcNames = "N/A";
        if (b.getAppointment() != null &&
                b.getAppointment().getServices() != null &&
                !b.getAppointment().getServices().isEmpty()) {
            svcNames = b.getAppointment().getServices().stream()
                    .map(Services::getName)
                    .collect(Collectors.joining(", "));
        }

        return new BillDto(
                b.getBillId(),
                b.getAppointment() != null ? b.getAppointment().getAppointmentId() : null,
                custName,
                svcNames,
                b.getAmount(),
                b.getPaymentStatus() != null ? b.getPaymentStatus().name() : "UNKNOWN",
                b.getCreatedAt()
        );
    }

    public List<BillDto> getAllBillDtos() { return billsRepo.findAll().stream().map(this::toDto).collect(Collectors.toList()); }
    public Optional<BillDto> getBillDto(Long id) { return billsRepo.findById(id).map(this::toDto); }
    public List<BillDto> getBillDtosByCustomer(Long cid) { return getCustomerBills(cid).stream().map(this::toDto).collect(Collectors.toList()); }
    public List<BillDto> getBillDtosByStatus(Bills.PaymentStatus s) { return getBillsByStatus(s).stream().map(this::toDto).collect(Collectors.toList()); }
    public List<BillDto> getBillDtosByDateRange(LocalDateTime from, LocalDateTime to) { return getBillsByDateRange(from, to).stream().map(this::toDto).collect(Collectors.toList()); }
}
