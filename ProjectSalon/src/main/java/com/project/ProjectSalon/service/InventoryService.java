package com.project.ProjectSalon.service;


import com.project.ProjectSalon.entity.Inventory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface InventoryService {

    List<Inventory> getAllInvent();

    Optional<Inventory> getInventId(Long inventId);

    List<Inventory> searchItems(String name);

    List<Inventory> getLowStockItems();

    Inventory createItem(Inventory inventory);

    Inventory updateItem(Long inventId, Inventory inventory);

    Inventory updateStock(Long inventId, Integer quantity);

    void deleteItem(Long inventId);

}
