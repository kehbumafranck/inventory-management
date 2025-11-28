package com.quamtech.inventory_management.controller;

import com.quamtech.inventory_management.entite.StockMovement;
import com.quamtech.inventory_management.enumeration.MovementType;
import com.quamtech.inventory_management.exception.InventoryException;
import com.quamtech.inventory_management.service.StockMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockMovementController {
    private final StockMovementService movementService;

    @PostMapping("/createMouvement")
    public ResponseEntity<StockMovement> createMovement(@Valid @RequestBody StockMovement movement,
                                                        @RequestParam String userId) throws InventoryException {
        return new ResponseEntity<>(movementService.createMovement(movement, userId), HttpStatus.CREATED);
    }

    @GetMapping("/getMouvementById/{id}")
    public ResponseEntity<StockMovement> getMovement(@PathVariable String id) {
        return ResponseEntity.ok(movementService.getMovementById(id));
    }

    @GetMapping("/getAllMouvement")
    public ResponseEntity<List<StockMovement>> getAllMovements() {
        return ResponseEntity.ok(movementService.getAllMovements());
    }

    @GetMapping("/getMovementsByProduct/{productId}")
    public ResponseEntity<List<StockMovement>> getMovementsByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(movementService.getMovementsByProduct(productId));
    }

    @GetMapping("/getMovementsByWarehouse/{warehouseId}")
    public ResponseEntity<List<StockMovement>> getMovementsByWarehouse(@PathVariable String warehouseId) {
        return ResponseEntity.ok(movementService.getMovementsByWarehouse(warehouseId));
    }

    @GetMapping("/getMovementsByType/{type}")
    public ResponseEntity<List<StockMovement>> getMovementsByType(@PathVariable MovementType type) {
        return ResponseEntity.ok(movementService.getMovementsByType(type));
    }

    @GetMapping("/getMovementsByReference/{reference}")
    public ResponseEntity<List<StockMovement>> getMovementsByReference(@PathVariable String reference) {
        return ResponseEntity.ok(movementService.getMovementsByReference(reference));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<StockMovement>> getMovementsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(movementService.getMovementsByDateRange(start, end));
    }

    @GetMapping("/getProductHistory")
    public ResponseEntity<List<StockMovement>> getProductHistory(
            @RequestParam String productId,
            @RequestParam String warehouseId) {
        return ResponseEntity.ok(movementService.getProductHistory(productId, warehouseId));
    }
}
