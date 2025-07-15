package com.project.ProjectSalon.serviceimp;

import com.project.ProjectSalon.entity.*;
import com.project.ProjectSalon.repo.*;
import com.project.ProjectSalon.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImp implements AppointmentService {

    /* ─────────────── repositories ─────────────── */
    @Autowired private AppointmentRepository appointmentRepo;
    @Autowired private CustomerRepo          customerRepo;
    @Autowired private StaffRepo             staffRepo;
    @Autowired private ServiceRepo           serviceRepo;

    /* ─────────────────── READ ─────────────────── */

    @Override
    public List<Appointment> getByCustomer(Long userId) {
        Customers cust = customerRepo.findByUsers_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found for userId " + userId));
        return appointmentRepo.findByCustomers_CustomerId(cust.getCustomerId());
    }

    @Override
    public List<Appointment> getByStaff(Long userId) {
        Staff st = staffRepo.findByUsers_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Staff not found for userId " + userId));
        return appointmentRepo.findByStaff_StaffId(st.getStaffId());
    }

    @Override public List<Appointment> getAllAppointment()            { return appointmentRepo.findAll(); }
    @Override public Optional<Appointment> getAppointmentId(Long id)  { return appointmentRepo.findById(id); }

    /* ────────────────── CREATE ────────────────── */

    /**
     * Expected JSON (examples):
     * Customer flow ⇒ auto‑assign staff<br>
     * { "customers": { "customerId": 5 }, "services":[{ "serviceId":1 }], ... }
     * <br><br>
     * Staff flow ⇒ explicit staffId<br>
     * { "customers": { "customerId": 5 }, "staff": { "staffId": 3 }, "services":[...], ... }
     */
    @Override
    public Appointment createAppointment(Appointment a) {

        /* 1️⃣ customer must exist */
        if (a.getCustomers() == null || a.getCustomers().getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        Customers cust = customerRepo.findById(a.getCustomers().getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        /* 2️⃣ resolve services to managed entities */
        List<Services> services = a.getServices().stream()
                .map(s -> serviceRepo.findById(s.getServiceId())
                        .orElseThrow(() -> new RuntimeException("Invalid serviceId " + s.getServiceId())))
                .collect(Collectors.toList());

        /* 3️⃣ determine which staff member to use */
        Staff chosen;
        if (a.getStaff() != null && a.getStaff().getStaffId() != null) {
            // staff explicitly provided in the payload
            chosen = staffRepo.findById(a.getStaff().getStaffId())
                    .orElseThrow(() -> new RuntimeException("Staff not found (id " + a.getStaff().getStaffId() + ")"));
        } else {
            // no staff provided ⇒ auto‑assign first available
            chosen = staffRepo.findAllByOrderByStaffIdAsc().stream()
                    .filter(st -> appointmentRepo.findConflicts(
                            st.getStaffId(), a.getAppointmentDate(),
                            a.getStartTime(), a.getEndTime()).isEmpty())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No staff available for chosen time"));
        }

        /* 4️⃣ availability check for the chosen staff*/
        boolean clash = !appointmentRepo.findConflicts(
                chosen.getStaffId(), a.getAppointmentDate(),
                a.getStartTime(), a.getEndTime()).isEmpty();
        if (clash) {
            throw new RuntimeException("Selected staff is not free at the requested time");
        }

        /* 5️⃣ persist */
        a.setCustomers(cust);
        a.setServices(services);
        a.setStaff(chosen);
        a.setStatus(Appointment.AppointmentStatus.SCHEDULED);

        return appointmentRepo.save(a);
    }

    /* ─────────────── UPDATE / DELETE ─────────────── */

    @Override
    public void deleteAppointment(Long id) {
        appointmentRepo.deleteById(id);
    }

    @Override
    public Appointment updateAppointment(Long id, Appointment patch) {
        Appointment appt = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (patch.getStatus()          != null) appt.setStatus(patch.getStatus());
        if (patch.getAppointmentDate() != null) appt.setAppointmentDate(patch.getAppointmentDate());
        if (patch.getStartTime()       != null) appt.setStartTime(patch.getStartTime());
        if (patch.getEndTime()         != null) appt.setEndTime(patch.getEndTime());

        if (patch.getStaff() != null && patch.getStaff().getStaffId() != null) {
            Staff st = staffRepo.findById(patch.getStaff().getStaffId())
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            appt.setStaff(st);
        }
        return appointmentRepo.save(appt);
    }

    /** Partial helper: update status only */
    public void updateStatus(Long id, String status) {
        if (status == null) throw new IllegalArgumentException("Status is required");
        Appointment appt = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appt.setStatus(Appointment.AppointmentStatus.valueOf(status.toUpperCase()));
        appointmentRepo.save(appt);
    }
}
