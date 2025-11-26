package com.quamtech.inventory_management.controller;

import com.quamtech.inventory_management.entite.InventoryReport;
import com.quamtech.inventory_management.enumeration.ReportType;
import com.quamtech.inventory_management.service.InventoryReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryReportController {
    private final InventoryReportService reportService;

    // ========== RAPPORTS STANDARDS ==========

    @PostMapping("/generateStockStatusReport")
    public ResponseEntity<InventoryReport> generateStockStatusReport(
            @RequestParam(required = false) String warehouseId,
            @RequestParam String userId) {
        return ResponseEntity.ok(reportService.generateStockStatusReport(warehouseId, userId));
    }

    @PostMapping("/generateMovementsReport")
    public ResponseEntity<InventoryReport> generateMovementsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String warehouseId,
            @RequestParam String userId) {
        return ResponseEntity.ok(reportService.generateMovementsReport(startDate, endDate, warehouseId, userId));
    }

    @PostMapping("/generateLowStockAlertReport")
    public ResponseEntity<InventoryReport> generateLowStockAlertReport(
            @RequestParam(required = false) String warehouseId,
            @RequestParam String userId) {
        return ResponseEntity.ok(reportService.generateLowStockAlertReport(warehouseId, userId));
    }

    // ========== INVENTAIRE PHYSIQUE ==========

//    @PostMapping("/physical-inventory")
//    public ResponseEntity<InventoryReport> createPhysicalInventory(@RequestBody Map<String, Object> request) {
//        String title = (String) request.get("title");
//        String warehouseId = (String) request.get("warehouseId");
//        @SuppressWarnings("unchecked")
//        List<String> locationIds = (List<String>) request.get("locationIds");
//        String userId = (String) request.get("userId");
//
//        return new ResponseEntity<>(
//                reportService.createPhysicalInventory(title, warehouseId, locationIds, userId),
//                HttpStatus.CREATED
//        );
//    }
//
//    @PutMapping("/physical-inventory/{id}/start")
//    public ResponseEntity<InventoryReport> startPhysicalInventory(@PathVariable String id) {
//        return ResponseEntity.ok(reportService.startPhysicalInventory(id));
//    }
//
//    @PutMapping("/physical-inventory/{id}/update-line")
//    public ResponseEntity<InventoryReport> updateInventoryLine(
//            @PathVariable String id,
//            @RequestBody Map<String, Object> request) {
//        String productId = (String) request.get("productId");
//        String locationId = (String) request.get("locationId");
//        Integer countedQuantity = (Integer) request.get("countedQuantity");
//        String countedBy = (String) request.get("countedBy");
//
//        return ResponseEntity.ok(reportService.updateInventoryLine(
//                id, productId, locationId, countedQuantity, countedBy));
//    }
//
//    @PutMapping("/physical-inventory/{id}/reconcile-line")
//    public ResponseEntity<InventoryReport> reconcileLine(
//            @PathVariable String id,
//            @RequestBody Map<String, String> request,
//            @RequestParam String userId) {
//        return ResponseEntity.ok(reportService.reconcileInventoryLine(
//                id, request.get("productId"), request.get("locationId"), userId));
//    }
//
//    @PutMapping("/physical-inventory/{id}/reconcile-all")
//    public ResponseEntity<InventoryReport> reconcileAll(
//            @PathVariable String id,
//            @RequestParam String userId) {
//        return ResponseEntity.ok(reportService.reconcileAllLines(id, userId));
//    }
//
//    @PutMapping("/physical-inventory/{id}/complete")
//    public ResponseEntity<InventoryReport> completePhysicalInventory(@PathVariable String id) {
//        return ResponseEntity.ok(reportService.completePhysicalInventory(id));
//    }

    // ========== CONSULTATION RAPPORTS ==========

    @GetMapping("/getReport/{id}")
    public ResponseEntity<InventoryReport> getReport(@PathVariable String id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @GetMapping("/getAllReports")
    public ResponseEntity<List<InventoryReport>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/getReportsByType/{type}")
    public ResponseEntity<List<InventoryReport>> getReportsByType(@PathVariable ReportType type) {
        return ResponseEntity.ok(reportService.getReportsByType(type));
    }

    @GetMapping("/ getPhysicalInventoriesByStatus/status/{status}")
    public ResponseEntity<List<InventoryReport>> getPhysicalInventoriesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(reportService.getPhysicalInventoriesByStatus(status));
    }

    @DeleteMapping("/deleteReport/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable String id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
    // ========== GÉNÉRATION PDF ==========

    @GetMapping("/generateStockStatusReportPdf/pdf")
    public ResponseEntity<byte[]> generateStockStatusReportPdf(
            @RequestParam(required = false) String warehouseId,
            @RequestParam String userId) {
        byte[] pdfBytes = reportService.generateStockStatusReportPdf(warehouseId, userId);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=rapport-etat-stocks.pdf")
                .body(pdfBytes);
    }

    @GetMapping("/generateMovementsReportPdf/pdf")
    public ResponseEntity<byte[]> generateMovementsReportPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String warehouseId,
            @RequestParam String userId) {
        byte[] pdfBytes = reportService.generateMovementsReportPdf(startDate, endDate, warehouseId, userId);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=rapport-mouvements-stocks.pdf")
                .body(pdfBytes);
    }

    @GetMapping("/generateLowStockAlertReportPdf/pdf")
    public ResponseEntity<byte[]> generateLowStockAlertReportPdf(
            @RequestParam(required = false) String warehouseId,
            @RequestParam String userId) {
        byte[] pdfBytes = reportService.generateLowStockAlertReportPdf(warehouseId, userId);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=rapport-alertes-stocks.pdf")
                .body(pdfBytes);
    }

    @GetMapping("/generatePdfFromExistingReport/{id}/pdf")
    public ResponseEntity<byte[]> generatePdfFromExistingReport(@PathVariable String id) {
        byte[] pdfBytes = reportService.generatePdfFromExistingReport(id);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=rapport-" + id + ".pdf")
                .body(pdfBytes);
    }
}
