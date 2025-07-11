package com.project.ProjectSalon.repo;

import com.project.ProjectSalon.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory ,Long> {

    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.reorderLevel")
    List<Inventory> findItemsNeedingReorder();

    List<Inventory> findByNameContainingIgnoreCase(String name);
}
