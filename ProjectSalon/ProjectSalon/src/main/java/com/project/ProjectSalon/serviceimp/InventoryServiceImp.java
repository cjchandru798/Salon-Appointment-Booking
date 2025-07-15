package com.project.ProjectSalon.serviceimp;

import com.project.ProjectSalon.entity.Inventory;
import com.project.ProjectSalon.repo.InventoryRepo;
import com.project.ProjectSalon.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InventoryServiceImp implements InventoryService {

    @Autowired
    InventoryRepo inventoryRepo;

    @Override
    public List<Inventory> getAllInvent() {
        return inventoryRepo.findAll();
    }

    @Override
    public Optional<Inventory> getInventId(Long inventId) {
        return inventoryRepo.findById(inventId);
    }

    @Override
    public List<Inventory> searchItems(String name) {
        return inventoryRepo.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Inventory> getLowStockItems() {
        return inventoryRepo.findItemsNeedingReorder();
    }

    @Override
    public Inventory createItem(Inventory inventory) {
        return inventoryRepo.save(inventory);
    }

    @Override
    public Inventory updateItem(Long inventId, Inventory inventory) {
        Inventory update = inventoryRepo.findById(inventId)
                .orElseThrow(()-> new RuntimeException("Not found"));
        update.setName(inventory.getName());
        update.setDescription(inventory.getDescription());
        update.setQuantity(inventory.getQuantity());
        update.setUnitPrice(inventory.getUnitPrice());
        update.setReorderLevel(inventory.getReorderLevel());
        return inventoryRepo.save(update);
    }

    @Override
    public Inventory updateStock(Long inventId, Integer quantity) {
        Inventory updateStock = inventoryRepo.findById(inventId)
                .orElseThrow(()->new RuntimeException("Not Found"));

        updateStock.setQuantity(updateStock.getQuantity()+quantity);
        return inventoryRepo.save(updateStock);
    }

    @Override
    public void deleteItem(Long inventId) {
        inventoryRepo.deleteById(inventId);

    }
}
