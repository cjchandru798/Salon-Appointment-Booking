package com.project.ProjectSalon.controller;

import com.project.ProjectSalon.entity.Inventory;
import com.project.ProjectSalon.serviceimp.InventoryServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    InventoryServiceImp inventoryServiceImp;


    @GetMapping("/getall")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<Inventory> getAllItems(){
        return inventoryServiceImp.getAllInvent();
    }
    @GetMapping("/{inventId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Optional<Inventory>> getItemById(@PathVariable Long inventId) {
        return ResponseEntity.ok(inventoryServiceImp.getInventId(inventId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<Inventory> searchItems(@RequestParam String name) {
        return inventoryServiceImp.searchItems(name);
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<Inventory> getLowStockItems() {
        return inventoryServiceImp.getLowStockItems();
    }
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Inventory> createItem(@RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryServiceImp.createItem(inventory));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Inventory> updateItem(
            @PathVariable Long inventId,
            @RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryServiceImp.updateItem(inventId,inventory));
    }

    @PatchMapping("/stock/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Inventory> updateStock(
            @PathVariable Long inventId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryServiceImp.updateStock(inventId,quantity));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteItem(@PathVariable Long inventId) {
       inventoryServiceImp.deleteItem(inventId);
        return ResponseEntity.ok().build();
    }

}
