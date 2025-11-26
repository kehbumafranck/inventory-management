package com.quamtech.inventory_management.controller;

import com.quamtech.inventory_management.entite.InventoryReport;
import com.quamtech.inventory_management.enumeration.ReportType;
import com.quamtech.inventory_management.service.InventoryReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryReportController {
    private final InventoryReportService reportService;

    @PostMapping("/stock-status")
    public ResponseEntity<InventoryReport> generateStockStatusReport(
            @RequestParam(required = false) String warehouseId,
            @RequestParam String userId) {
        return ResponseEntity.ok(reportService.generateStockStatusReport(warehouseId, userId));
    }

    @PostMapping("/movements")
    public ResponseEntity<InventoryReport> generateMovementsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String warehouseId,
            @RequestParam String userId) {
        return ResponseEntity.ok(reportService.generateMovementsReport(startDate, endDate, warehouseId, userId));
    }

    @PostMapping("/low-stock")
    public ResponseEntity<InventoryReport> generateLowStockAlertReport(
            @RequestParam(required = false) String warehouseId,
            @RequestParam String userId) {
        return ResponseEntity.ok(reportService.generateLowStockAlertReport(warehouseId, userId));
    }

//    @PostMapping("/reorder")
//    public ResponseEntity<InventoryReport> generateReorderSuggestionReport(
//            @RequestParam(required = false) String warehouseId,
//            @RequestParam String userId) {
//        return ResponseEntity.ok(reportService.generateReorderSuggestionReport(warehouseId, userId));
//    }
//
//    @PostMapping("/valuation")
//    public ResponseEntity<InventoryReport> generateValuationReport(
//            @RequestParam(required = false) String warehouseId,
//            @RequestParam String userId) {
//        return ResponseEntity.ok(reportService.generateValuationReport(warehouseId, userId));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryReport> getReport(@PathVariable String id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @GetMapping
    public ResponseEntity<List<InventoryReport>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<InventoryReport>> getReportsByType(@PathVariable ReportType type) {
        return ResponseEntity.ok(reportService.getReportsByType(type));
    }

//    @GetMapping("/warehouse/{warehouseId}")
//    public ResponseEntity<List<InventoryReport>> getReportsByWarehouse(@PathVariable String warehouseId) {
//        return ResponseEntity.ok(reportService.getReportsByWarehouse(warehouseId));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable String id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
