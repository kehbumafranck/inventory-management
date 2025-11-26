package com.quamtech.inventory_management.controller;

import com.quamtech.inventory_management.entite.Stock;
import com.quamtech.inventory_management.payload.StockAlertInfo;
import com.quamtech.inventory_management.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockController {
    private final StockService stockService;

    @PostMapping("/createOrUpdateStock")
    public ResponseEntity<Stock> createOrUpdateStock(@Valid @RequestBody Stock stock,
                                                     @RequestParam String userId) {
        return new ResponseEntity<>(stockService.createOrUpdateStock(stock, userId), HttpStatus.CREATED);
    }

    @GetMapping("/getStockById/{id}")
    public ResponseEntity<Stock> getStock(@PathVariable String id) {
        return ResponseEntity.ok(stockService.getStockById(id));
    }

    @GetMapping("getAllStocks")
    public ResponseEntity<List<Stock>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @GetMapping("/getStocksByProduct/{productId}")
    public ResponseEntity<List<Stock>> getStocksByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(stockService.getStocksByProduct(productId));
    }

    @GetMapping("/getStocksByWarehouse/{warehouseId}")
    public ResponseEntity<List<Stock>> getStocksByWarehouse(@PathVariable String warehouseId) {
        return ResponseEntity.ok(stockService.getStocksByWarehouse(warehouseId));
    }

    @GetMapping("/getTotalQuantity/{productId}/total")
    public ResponseEntity<Integer> getTotalQuantity(@PathVariable String productId) {
        return ResponseEntity.ok(stockService.getTotalQuantityByProduct(productId));
    }

    @GetMapping("/getAvailableQuantity/{productId}/available")
    public ResponseEntity<Integer> getAvailableQuantity(@PathVariable String productId) {
        return ResponseEntity.ok(stockService.getAvailableQuantityByProduct(productId));
    }

    @PostMapping("/reserveStock")
    public ResponseEntity<Void> reserveStock(@RequestBody Map<String, Object> request) {
        String productId = (String) request.get("productId");
        String warehouseId = (String) request.get("warehouseId");
        String locationId = (String) request.get("locationId");
        String lotId = (String) request.get("lotId");
        Integer quantity = (Integer) request.get("quantity");
        String userId = (String) request.get("userId");

        stockService.reserveStock(productId, warehouseId, locationId, lotId, quantity, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/releaseReservation")
    public ResponseEntity<Void> releaseReservation(@RequestBody Map<String, Object> request) {
        String productId = (String) request.get("productId");
        String warehouseId = (String) request.get("warehouseId");
        String locationId = (String) request.get("locationId");
        String lotId = (String) request.get("lotId");
        Integer quantity = (Integer) request.get("quantity");
        String userId = (String) request.get("userId");

        stockService.releaseReservation(productId, warehouseId, locationId, lotId, quantity, userId);
        return ResponseEntity.ok().build();
    }

    // Endpoint pour les alertes de stock (MÉTHODE, pas une classe)
    @GetMapping("/getStockAlerts")
    public ResponseEntity<List<StockAlertInfo>> getStockAlerts(
            @RequestParam(required = false) String warehouseId) {
        return ResponseEntity.ok(stockService.checkStockAlerts(warehouseId));
    }
    //Obtenir des alert critique
    @GetMapping("/getCriticalAlerts")
    public ResponseEntity<List<StockAlertInfo>> getCriticalAlerts() {
        return ResponseEntity.ok(stockService.getCriticalStockAlerts());
    }
    //Obtenir des suggestions de réapprovisionnement
    @GetMapping("/getReorderSuggestions")
    public ResponseEntity<List<StockAlertInfo>> getReorderSuggestions() {
        return ResponseEntity.ok(stockService.getReorderSuggestions());
    }

    @DeleteMapping("/deleteStock/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable String id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}
