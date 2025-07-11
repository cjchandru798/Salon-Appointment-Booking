package com.project.ProjectSalon.serviceimp;

import com.project.ProjectSalon.entity.Staff;
import com.project.ProjectSalon.entity.Users;
import com.project.ProjectSalon.repo.StaffRepo;
import com.project.ProjectSalon.repo.UsersRepo;
import com.project.ProjectSalon.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffServiceImp implements StaffService {

    @Autowired
    private StaffRepo staffRepo;

    @Autowired
    private UsersRepo usersRepo;

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
        if (staff.getUsers() == null || staff.getUsers().getUserId() == null) {
            throw new IllegalArgumentException("User must be provided for staff");
        }

        Users user = usersRepo.findById(staff.getUsers().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        staff.setUsers(user);
        return staffRepo.save(staff);
    }

    @Override
    public Staff updateStaff(Long staffId, Staff staff) {
       Staff update = staffRepo.findById(staffId).orElseThrow(()-> new RuntimeException("Staff not found with id: " + staffId));

       update.setFirstName(staff.getFirstName());
       update.setLastName(staff.getLastName());
       update.setPhone(staff.getPhone());
       update.setSpecialization(staff.getSpecialization());
        return staffRepo.save(update);
    }

    @Override
    public void deletStaff(Long staffId) {
        staffRepo.deleteById(staffId);
    }


}



