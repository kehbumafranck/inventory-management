package com.quamtech.inventory_management.controller;

import com.quamtech.inventory_management.entite.Warehouse;
import com.quamtech.inventory_management.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PostMapping(value = "/createEntrepot")
    public ResponseEntity<Warehouse> createWarehouse(@Valid @RequestBody Warehouse warehouse) {
        return new ResponseEntity<>(warehouseService.createWarehouse(warehouse), HttpStatus.CREATED);
    }

    @PutMapping("/updateWarehouse/{id}")
    public ResponseEntity<Warehouse> updateWarehouse(@PathVariable String id, @Valid @RequestBody Warehouse warehouse) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouse));
    }

    @GetMapping("/getwarehouse/{id}")
    public ResponseEntity<Warehouse> getWarehouse(@PathVariable String id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @GetMapping("getAllWarehouse")
    public ResponseEntity<List<Warehouse>> getAllWarehouses(@RequestParam(required = false) Boolean active) {
        if (active != null && active) {
            return ResponseEntity.ok(warehouseService.getActiveWarehouses());
        }
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @DeleteMapping("deleteWarehouse/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable String id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}
