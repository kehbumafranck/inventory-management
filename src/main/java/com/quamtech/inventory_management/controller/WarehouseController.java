package com.quamtech.inventory_management.controller;

import com.quamtech.inventory_management.entite.Warehouse;
import com.quamtech.inventory_management.exception.InventoryException;
import com.quamtech.inventory_management.payload.ApiResponse;
import com.quamtech.inventory_management.payload.request.WarehouseRequest;
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
    public ResponseEntity<ApiResponse<Warehouse>> createWarehouse(@Valid @RequestBody WarehouseRequest warehouse) {
        Warehouse warehouse1=warehouseService.createWarehouse(warehouse);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("entrepot créé avec succès", warehouse1));
    }

    @PutMapping("/updateWarehouse/{id}")
    public ResponseEntity<ApiResponse<Warehouse>> updateWarehouse(@PathVariable String id, @Valid @RequestBody WarehouseRequest warehouse) throws InventoryException {
        Warehouse warehouse1=warehouseService.updateWarehouse(id,warehouse);
        return ResponseEntity.ok(ApiResponse.success("l'entrepot  mis à jour", warehouse1));
    }

    @GetMapping("/getwarehouse/{id}")
    public ResponseEntity<ApiResponse<Warehouse>> getWarehouse(@PathVariable String id) throws InventoryException {
        Warehouse warehouse=warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(ApiResponse.success("Liste d'entrpot", warehouse));
    }

    @GetMapping("getAllWarehouse")
    public ResponseEntity<ApiResponse<List<Warehouse>>> getAllWarehouses(@RequestParam(required = false) Boolean active) {
        List<Warehouse> warehouseList=warehouseService.getAllWarehouses();
        if (active != null && active) {
            return ResponseEntity.ok(ApiResponse.success("les entrpot actif sont", warehouseList));
        }
        return ResponseEntity.ok(ApiResponse.success("les entrpot non actif sont", warehouseList));
    }

    @DeleteMapping("deleteWarehouse/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable String id) throws InventoryException {
        warehouseService.deleteWarehouse(id);
        return  ResponseEntity.ok(ApiResponse.success("entrepot supprimé avec succes", null));
    }
}
