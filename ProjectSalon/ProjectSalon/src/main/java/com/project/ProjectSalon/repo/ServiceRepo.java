package com.project.ProjectSalon.repo;

import com.project.ProjectSalon.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepo extends JpaRepository<Services, Long> {
    List<Services> findByCategory(String category);
    List<Services> findAllById(Iterable<Long> ids);


}
