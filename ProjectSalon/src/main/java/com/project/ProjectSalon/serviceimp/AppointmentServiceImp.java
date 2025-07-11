package com.project.ProjectSalon.serviceimp;

import com.project.ProjectSalon.entity.Appointment;
import com.project.ProjectSalon.entity.Customers;
import com.project.ProjectSalon.entity.Services;
import com.project.ProjectSalon.entity.Staff;
import com.project.ProjectSalon.repo.AppointmentRepository;
import com.project.ProjectSalon.repo.CustomerRepo;
import com.project.ProjectSalon.repo.ServiceRepo;
import com.project.ProjectSalon.repo.StaffRepo;
import com.project.ProjectSalon.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AppointmentServiceImp implements AppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    StaffRepo staffRepo;

    @Autowired
    ServiceRepo serviceRepo;


    @Override
    public List<Appointment> getAllAppointment() {
        return appointmentRepository.findAll();
    }

    @Override
    public Optional<Appointment> getAppointmentId(Long appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    @Override
    public Appointment createAppointment(Appointment appointment) {

        Customers customers =customerRepo.findById(appointment.getCustomers().getCustomerId())
                .orElseThrow(()->new RuntimeException("Customer Not found"));

        Staff staff =staffRepo.findById(appointment.getStaff().getStaffId())
                .orElseThrow(()-> new RuntimeException("Staff not found"));

        Services services = serviceRepo.findById(appointment.getServices().getServiceId())
                .orElseThrow(()->new RuntimeException("Service Not found"));


        Appointment appointment1 = new Appointment();

        appointment1.setCustomers(customers);
        appointment1.setStaff(staff);
        appointment1.setServices(services);
        appointment1.setAppointmentDate(appointment.getAppointmentDate());
        appointment1.setStartTime(appointment.getStartTime());
        appointment1.setEndTime(appointment.getEndTime());
        appointment1.setStatus(appointment.getStatus());
        return appointmentRepository.save(appointment1);
    }

    @Override
    public void deleteAppointment(Long appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }


    @Override
    public Appointment updateAppointment(Long appointmentId, Appointment appointment) {

        Appointment update = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getCustomers() != null && appointment.getCustomers().getCustomerId() != null) {
            Customers customers = customerRepo.findById(appointment.getCustomers().getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer Not found"));
            update.setCustomers(customers);
        }
        if (appointment.getStaff() != null && appointment.getStaff().getStaffId() != null) {
            Staff staff = staffRepo.findById(appointment.getStaff().getStaffId())
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            update.setStaff(staff);
        }

        if (appointment.getServices() != null && appointment.getServices().getServiceId() != null) {
            Services services = serviceRepo.findById(appointment.getServices().getServiceId())
                    .orElseThrow(() -> new RuntimeException("Service Not found"));
            update.setServices(services);
        }

        update.setAppointmentDate(appointment.getAppointmentDate());
        update.setStartTime(appointment.getStartTime());
        update.setEndTime(appointment.getEndTime());
        update.setStatus(appointment.getStatus());

        return appointmentRepository.save(update);
    }


}
