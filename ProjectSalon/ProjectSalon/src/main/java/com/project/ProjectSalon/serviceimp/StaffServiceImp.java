// src/main/java/com/project/ProjectSalon/serviceimp/StaffServiceImp.java
package com.project.ProjectSalon.serviceimp;

import com.project.ProjectSalon.dto.StaffSummary;          // <- single DTO
import com.project.ProjectSalon.entity.Appointment;
import com.project.ProjectSalon.entity.Staff;
import com.project.ProjectSalon.entity.Users;
import com.project.ProjectSalon.repo.AppointmentRepository;
import com.project.ProjectSalon.repo.StaffRepo;
import com.project.ProjectSalon.repo.UsersRepo;
import com.project.ProjectSalon.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffServiceImp implements StaffService {

    @Autowired private StaffRepo             staffRepo;
    @Autowired private UsersRepo             usersRepo;
    @Autowired private AppointmentRepository appointmentRepo;

    /* ─────────────── CRUD ─────────────── */

    @Override
    public List<Staff> getAll() {
        return staffRepo.findAll();
    }

    @Override
    public Optional<Staff> getStaffId(Long staffId) {
        return staffRepo.findById(staffId);
    }

    @Override
    public Optional<Staff> getUserByStaff(Long userId) {
        return staffRepo.findByUsers_UserId(userId);
    }

    @Override
    public Staff createStaff(Staff staff) {
        if (staff.getUsers() == null || staff.getUsers().getUserId() == null)
            throw new IllegalArgumentException("User must be provided for staff");

        Users user = usersRepo.findById(staff.getUsers().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        staff.setUsers(user);
        return staffRepo.save(staff);
    }

    @Override
    public Staff updateStaff(Long staffId, Staff payload) {
        Staff st = staffRepo.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found: " + staffId));

        st.setFirstName     (payload.getFirstName());
        st.setLastName      (payload.getLastName());
        st.setPhone         (payload.getPhone());
        st.setSpecialization(payload.getSpecialization());
        return staffRepo.save(st);
    }

    @Override
    public void deleteStaff(Long staffId) { staffRepo.deleteById(staffId); }

    /* ─────────── Dashboard summary ─────────── */

    @Override
    public List<StaffSummary> buildSummary() {

        List<Staff>        staffList    = staffRepo.findAll();
        List<Appointment>  appointments = appointmentRepo.findAll();

        return staffList.stream().map(s -> {

            long total     = appointments.stream()
                    .filter(a -> a.getStaff().getStaffId().equals(s.getStaffId()))
                    .count();

            long completed = appointments.stream()
                    .filter(a -> a.getStaff().getStaffId().equals(s.getStaffId()))
                    .filter(a -> a.getStatus() == Appointment.AppointmentStatus.COMPLETED)
                    .count();

            long scheduled = appointments.stream()
                    .filter(a -> a.getStaff().getStaffId().equals(s.getStaffId()))
                    .filter(a -> a.getStatus() == Appointment.AppointmentStatus.SCHEDULED)
                    .count();

            return new StaffSummary(
                    s.getStaffId(),
                    s.getFirstName(),
                    s.getLastName(),
                    s.getPhone(),
                    s.getSpecialization(),
                    total,
                    completed,
                    scheduled
            );
        }).toList();
    }
}
