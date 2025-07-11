package com.project.ProjectSalon.serviceimp;

import com.project.ProjectSalon.entity.Services;
import com.project.ProjectSalon.repo.ServiceRepo;
import com.project.ProjectSalon.service.SalonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SalonServiceImp implements SalonService {

    @Autowired
    ServiceRepo serviceRepo;

    @Override
    public List<Services> getAllServices() {
        return serviceRepo.findAll();
    }

    @Override
    public Optional<Services> getServiceById(Long serviceId) {
        return serviceRepo.findById(serviceId);
    }

    @Override
    public Services createService(Services services) {
        return serviceRepo.save(services);
    }

    @Override
    public Services updateService(Long serviceId, Services services) {
        Services update = serviceRepo.findById(serviceId).orElseThrow(()-> new RuntimeException("Not found"));

        update.setName(services.getName());
        update.setDescription(services.getDescription());
        update.setDuration(services.getDuration());
        update.setPrice(services.getPrice());
        update.setCategory(services.getCategory());
        return serviceRepo.save(update);
    }

    @Override
    public void deleteService(Long serviceId) {
        serviceRepo.deleteById(serviceId);
    }

    @Override
    public List<Services> getServicesByCategory(String category) {
        return serviceRepo.findByCategory(category);
    }
}
