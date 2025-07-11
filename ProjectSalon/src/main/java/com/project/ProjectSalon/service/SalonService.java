package com.project.ProjectSalon.service;

import com.project.ProjectSalon.entity.Services;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SalonService {

    List<Services> getAllServices();

    Optional<Services> getServiceById(Long serviceId);

    Services createService(Services services);

    Services updateService(Long serviceId, Services services);

    void deleteService(Long serviceId);
    List<Services> getServicesByCategory(String category);
}
