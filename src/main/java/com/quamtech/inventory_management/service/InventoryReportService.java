package com.quamtech.inventory_management.service;

import com.quamtech.inventory_management.entite.*;
import com.quamtech.inventory_management.enumeration.MovementType;
import com.quamtech.inventory_management.enumeration.ReportType;
import com.quamtech.inventory_management.payload.StockAlertInfo;
import com.quamtech.inventory_management.repository.*;
import com.quamtech.inventory_management.utils.DateUtilitie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class InventoryReportService {

        private final PdfGenerationService pdfGenerationService;
        private final InventoryReportRepository reportRepository;
        private final StockService stockService;
        private final StockRepository stockRepository;
        private final ProductRepository productRepository;
        private final WarehouseRepository warehouseRepository;
        private final LocationRepository locationRepository;
        private final StockMovementRepository movementRepository;
        private final StockMovementService movementService;
    //génère automatiquement un rapport complet sur l’état des stocks
        public InventoryReport generateStockStatusReport(String warehouseId, String userId) {
            LocalDate  inventoryDate;
            try {
                inventoryDate = DateUtilitie.handleDate(String.valueOf(LocalDate.now()));
            } catch (Exception e) {
                throw new IllegalArgumentException("Date invalide : ");
            }
            InventoryReport report = new InventoryReport();
            report.setTitle("Rapport de l'état des stocks de l'entreprise QUANTUM TECHNOLOGIE");
            report.setReportType(ReportType.STOCK_STATUS);
            report.setGeneratedAt(inventoryDate);
            report.setGeneratedBy(userId);
            report.setWarehouseId(warehouseId);

            List<Stock> stocks = warehouseId != null ?
                    stockRepository.findByWarehouseId(warehouseId) :
                    stockRepository.findAllWithQuantity();

            List<InventoryReport.ReportItem> items = new ArrayList<>();
            int totalQuantity = 0;
            int totalReserved = 0;
            int totalAvailable = 0;
            double totalValue = 0.0;
            int lowStockCount = 0;
            int outOfStockCount = 0;
            int needsReorderCount = 0;

            for (Stock stock : stocks) {
                Product product = productRepository.findById(stock.getProductId()).orElse(null);
                if (product == null) continue;

                Warehouse warehouse = warehouseRepository.findById(stock.getWarehouseId()).orElse(null);
                Location location = stock.getLocationId() != null ?
                        locationRepository.findById(stock.getLocationId()).orElse(null) : null;

                InventoryReport.ReportItem item = new InventoryReport.ReportItem();
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setSku(product.getSku());
                item.setWarehouseName(warehouse != null ? warehouse.getName() : "N/A");
                item.setLocationCode(location != null ? location.getCode() : "N/A");
                item.setLotNumber(stock.getLotNumber());
                item.setSerialNumber(stock.getSerialNumber());

                item.setQuantity(stock.getQuantity());
                item.setReservedQuantity(stock.getReservedQuantity());
                item.setAvailableQuantity(stock.getAvailableQuantity());

                item.setUnitPrice(product.getUnitPrice());
                double itemValue = stock.getQuantity() * product.getUnitPrice();
                item.setTotalValue(itemValue);

                items.add(item);

                totalQuantity += stock.getQuantity();
                totalReserved += stock.getReservedQuantity();
                totalAvailable += stock.getAvailableQuantity();
                totalValue += itemValue;

                if (stock.getAvailableQuantity() == 0) {
                    outOfStockCount++;
                } else if (stock.getAvailableQuantity() < product.getMinimumThreshold()) {
                    lowStockCount++;
                } else if (stock.getAvailableQuantity() <= product.getReorderLevel()) {
                    needsReorderCount++;
                }
            }

            report.setItems(items);

            InventoryReport.ReportSummary summary = new InventoryReport.ReportSummary();
            summary.setTotalProducts(items.size());
            summary.setTotalQuantity(totalQuantity);
            summary.setTotalReserved(totalReserved);
            summary.setTotalAvailable(totalAvailable);
            summary.setTotalValue(totalValue);
            summary.setLowStockProducts(lowStockCount);
            summary.setOutOfStockProducts(outOfStockCount);
            summary.setNeedsReorderProducts(needsReorderCount);
            report.setSummary(summary);

            return reportRepository.save(report);
        }
        //génère un rapport des mouvements de stock
        public InventoryReport generateMovementsReport(LocalDateTime startDate, LocalDateTime endDate,
                                                       String warehouseId, String userId) {
            LocalDate  inventoryDate;
            try {
                inventoryDate = DateUtilitie.handleDate(String.valueOf(LocalDate.now()));
            } catch (Exception e) {
                throw new IllegalArgumentException("Date invalide : ");
            }
            InventoryReport report = new InventoryReport();
            report.setTitle("Rapport des mouvements de stock");
            report.setReportType(ReportType.STOCK_MOVEMENTS);
            report.setGeneratedAt(inventoryDate);
            report.setGeneratedBy(userId);
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setWarehouseId(warehouseId);

            List<StockMovement> movements = movementRepository.findByMovementDateBetween(startDate, endDate);

            if (warehouseId != null) {
                movements = movements.stream()
                        .filter(m -> m.getWarehouseId().equals(warehouseId))
                        .collect(Collectors.toList());
            }

            List<InventoryReport.ReportItem> items = movements.stream()
                    .collect(Collectors.groupingBy(StockMovement::getProductId))
                    .entrySet().stream()
                    .map(entry -> {
                        String productId = entry.getKey();
                        List<StockMovement> productMovements = entry.getValue();
                        Product product = productRepository.findById(productId).orElse(null);

                        if (product == null) return null;

                        InventoryReport.ReportItem item = new InventoryReport.ReportItem();
                        item.setProductId(productId);
                        item.setProductName(product.getName());
                        item.setSku(product.getSku());
                        item.setMovements(productMovements.size());

                        int totalQuantity = productMovements.stream()
                                .mapToInt(StockMovement::getQuantity)
                                .sum();
                        item.setQuantity(totalQuantity);

                        double totalValue = productMovements.stream()
                                .filter(m -> m.getTotalCost() != null)
                                .mapToDouble(StockMovement::getTotalCost)
                                .sum();
                        item.setTotalValue(totalValue);

                        return item;
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());

            report.setItems(items);

            InventoryReport.ReportSummary summary = new InventoryReport.ReportSummary();
            summary.setTotalProducts(items.size());
            summary.setTotalMovements(movements.size());
            summary.setTotalQuantity(items.stream().mapToInt(InventoryReport.ReportItem::getQuantity).sum());
            summary.setTotalValue(items.stream().mapToDouble(InventoryReport.ReportItem::getTotalValue).sum());
            report.setSummary(summary);

            return reportRepository.save(report);
        }
        //génère un rapport des alert de stock
        public InventoryReport generateLowStockAlertReport(String warehouseId, String userId) {
            LocalDate  inventoryDate;
            try {
                inventoryDate = DateUtilitie.handleDate(String.valueOf(LocalDate.now()));
            } catch (Exception e) {
                throw new IllegalArgumentException("Date invalide : ");
            }
            InventoryReport report = new InventoryReport();
            report.setTitle("Rapport des alertes de stock faible");
            report.setReportType(ReportType.LOW_STOCK_ALERT);
            report.setGeneratedAt(inventoryDate);
            report.setGeneratedBy(userId);
            report.setWarehouseId(warehouseId);

            List<StockAlertInfo> alerts = stockService.checkStockAlerts(warehouseId);

            List<InventoryReport.ReportItem> items = alerts.stream()
                    .map(alert -> {
                        InventoryReport.ReportItem item = new InventoryReport.ReportItem();
                        item.setProductId(alert.getProductId());
                        item.setProductName(alert.getProductName());
                        item.setSku(alert.getSku());
                        item.setWarehouseName(alert.getWarehouseName());
                        item.setAvailableQuantity(alert.getAvailableQuantity());

                        Product product = productRepository.findById(alert.getProductId()).orElse(null);
                        if (product != null) {
                            item.setTotalValue(alert.getAvailableQuantity() * product.getUnitPrice());
                        }

                        return item;
                    })
                    .collect(Collectors.toList());

            report.setItems(items);

            InventoryReport.ReportSummary summary = new InventoryReport.ReportSummary();
            summary.setTotalProducts(items.size());
            long critical = alerts.stream()
                    .filter(a -> a.getAlertLevel() == StockAlertInfo.AlertLevel.CRITICAL)
                    .count();
            summary.setLowStockProducts((int) critical);
            summary.setTotalValue(items.stream().mapToDouble(InventoryReport.ReportItem::getTotalValue).sum());
            report.setSummary(summary);

            return reportRepository.save(report);
        }

        // INVENTAIRE PHYSIQUE - Créer un inventaire
//        public InventoryReport createPhysicalInventory(String title, String warehouseId,
//                                                       List<String> locationIds, String userId) {
//            LocalDate  inventoryDate;
//            try {
//                inventoryDate = DateUtilitie.handleDate(String.valueOf(LocalDate.now()));
//            } catch (Exception e) {
//                throw new IllegalArgumentException("Date invalide : ");
//            }
//            InventoryReport report = new InventoryReport();
//            report.setTitle(title);
//            report.setReportType(ReportType.PHYSICAL_INVENTORY);
//            report.setGeneratedAt(inventoryDate);
//            report.setGeneratedBy(userId);
//            report.setWarehouseId(warehouseId);
//            report.setPhysicalInventoryStatus("PLANNED");
//            report.setAssignedTo(userId);
//
//            // Initialiser les lignes avec les quantités système
//            List<Stock> stocks;
//            if (locationIds != null && !locationIds.isEmpty()) {
//                stocks = locationIds.stream()
//                        .flatMap(locId -> stockRepository.findByLocationId(locId).stream())
//                        .collect(Collectors.toList());
//            } else {
//                stocks = stockRepository.findByWarehouseId(warehouseId);
//            }
//
//            List<InventoryReport.ReportItem> items = new ArrayList<>();
//            for (Stock stock : stocks) {
//                Product product = productRepository.findById(stock.getProductId()).orElse(null);
//                if (product == null) continue;
//
//                InventoryReport.ReportItem item = new InventoryReport.ReportItem();
//                item.setProductId(stock.getProductId());
//                item.setProductName(product.getName());
//                item.setSku(product.getSku());
//                item.setLocationCode(stock.getLocationId());
//                item.setLotNumber(stock.getLotNumber());
//                item.setSerialNumber(stock.getSerialNumber());
//                item.setSystemQuantity(stock.getQuantity());
//                item.setCountedQuantity(null);
//                item.setVariance(null);
//                item.setReconciled(false);
//                items.add(item);
//            }
//
//            report.setItems(items);
//            return reportRepository.save(report);
//        }
//
//        // Démarrer l'inventaire
//        public InventoryReport startPhysicalInventory(String reportId) {
//            InventoryReport report = getReportById(reportId);
//            report.setPhysicalInventoryStatus("IN_PROGRESS");
//            report.setPhysicalInventoryDate(LocalDateTime.now());
//            return reportRepository.save(report);
//        }
//
//        // Mettre à jour une ligne de comptage
//        public InventoryReport updateInventoryLine(String reportId, String productId, String locationId,
//                                                   Integer countedQuantity, String countedBy) {
//            InventoryReport report = getReportById(reportId);
//
//            if (!"IN_PROGRESS".equals(report.getPhysicalInventoryStatus())) {
//                throw new RuntimeException("L'inventaire doit être en cours");
//            }
//
//            InventoryReport.ReportItem item = report.getItems().stream()
//                    .filter(i -> i.getProductId().equals(productId) &&
//                            (locationId == null || locationId.equals(i.getLocationCode())))
//                    .findFirst()
//                    .orElseThrow(() -> new RuntimeException("Ligne d'inventaire non trouvée"));
//
//            item.setCountedQuantity(countedQuantity);
//            item.setVariance(countedQuantity - item.getSystemQuantity());
//            item.setCountedBy(countedBy);
//            item.setCountedAt(LocalDateTime.now());
//
//            return reportRepository.save(report);
//        }

        // Réconcilier une ligne (créer ajustement)
//        public InventoryReport reconcileInventoryLine(String reportId, String productId,
//                                                      String locationId, String userId) {
//            InventoryReport report = getReportById(reportId);
//
//            InventoryReport.ReportItem item = report.getItems().stream()
//                    .filter(i -> i.getProductId().equals(productId) &&
//                            (locationId == null || locationId.equals(i.getLocationCode())))
//                    .findFirst()
//                    .orElseThrow(() -> new RuntimeException("Ligne non trouvée"));
//
//            if (item.getCountedQuantity() == null) {
//                throw new RuntimeException("Quantité comptée manquante");
//            }
//
//            if (item.getVariance() != 0) {
//                // Créer ajustement
//                StockMovement adjustment = new StockMovement();
//                adjustment.setProductId(item.getProductId());
//                adjustment.setWarehouseId(report.getWarehouseId());
//                adjustment.setLocationId(item.getLocationCode());
//                adjustment.setType(MovementType.INVENTORY_ADJUSTMENT);
//                adjustment.setQuantity(item.getCountedQuantity());
//                adjustment.setPhysicalInventoryId(reportId);
//                adjustment.setSystemQuantity(item.getSystemQuantity());
//                adjustment.setCountedQuantity(item.getCountedQuantity());
//                adjustment.setVariance(item.getVariance());
//                adjustment.setReference("INV-" + reportId);
//                adjustment.setReason("Ajustement inventaire physique");
//                adjustment.setNotes(String.format("Écart: %d", item.getVariance()));
//
//                movementService.createMovement(adjustment, userId);
//            }
//
//            item.setReconciled(true);
//            return reportRepository.save(report);
//        }

        // Réconcilier toutes les lignes
//        public InventoryReport reconcileAllLines(String reportId, String userId) {
//            InventoryReport report = getReportById(reportId);
//
//            for (InventoryReport.ReportItem item : report.getItems()) {
//                if (!item.isReconciled() && item.getCountedQuantity() != null) {
//                    reconcileInventoryLine(reportId, item.getProductId(), item.getLocationCode(), userId);
//                }
//            }
//
//            return reportRepository.findById(reportId).orElse(report);
//        }

//        // Compléter l'inventaire
//        public InventoryReport completePhysicalInventory(String reportId) {
//            InventoryReport report = getReportById(reportId);
//
//            long notCounted = report.getItems().stream()
//                    .filter(i -> i.getCountedQuantity() == null)
//                    .count();
//
//            if (notCounted > 0) {
//                throw new RuntimeException(
//                        String.format("%d ligne(s) non comptées", notCounted));
//            }
//
//            report.setPhysicalInventoryStatus("COMPLETED");
//
//            // Calculer le résumé
//            int totalVariance = report.getItems().stream()
//                    .mapToInt(i -> i.getVariance() != null ? i.getVariance() : 0)
//                    .sum();
//
//            double varianceValue = 0.0;
//            for (InventoryReport.ReportItem item : report.getItems()) {
//                if (item.getVariance() != null && item.getVariance() != 0) {
//                    Product product = productRepository.findById(item.getProductId()).orElse(null);
//                    if (product != null) {
//                        varianceValue += item.getVariance() * product.getUnitPrice();
//                    }
//                }
//            }
//
//            InventoryReport.ReportSummary summary = report.getSummary() != null ?
//                    report.getSummary() : new InventoryReport.ReportSummary();
//            summary.setTotalVariance(totalVariance);
//            summary.setVarianceValue(varianceValue);
//            summary.setItemsCounted(report.getItems().size());
//            summary.setItemsReconciled((int) report.getItems().stream().filter(InventoryReport.ReportItem::isReconciled).count());
//            report.setSummary(summary);
//
//            return reportRepository.save(report);
//        }

        public InventoryReport getReportById(String id) {
            return reportRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rapport non trouvé"));
        }

        public List<InventoryReport> getAllReports() {
            return reportRepository.findByOrderByGeneratedAtDesc();
        }

        public List<InventoryReport> getReportsByType(ReportType type) {
            return reportRepository.findByReportType(type);
        }

        public List<InventoryReport> getPhysicalInventoriesByStatus(String status) {
            return reportRepository.findByPhysicalInventoryStatus(status);
        }

        public void deleteReport(String id) {
            reportRepository.deleteById(id);
        }
    // ========== MÉTHODES DE GÉNÉRATION PDF ==========

    /**
     * Génère le rapport d'état des stocks en PDF
     */
    public byte[] generateStockStatusReportPdf(String warehouseId, String userId) {
        InventoryReport report = generateStockStatusReport(warehouseId, userId);
        return pdfGenerationService.generateStockStatusReportPdf(report);
    }

    /**
     * Génère le rapport des mouvements en PDF
     */
    public byte[] generateMovementsReportPdf(LocalDateTime startDate, LocalDateTime endDate,
                                             String warehouseId, String userId) {
        InventoryReport report = generateMovementsReport(startDate, endDate, warehouseId, userId);
        return pdfGenerationService.generateMovementsReportPdf(report);
    }

    /**
     * Génère le rapport des alertes de stock faible en PDF
     */
    public byte[] generateLowStockAlertReportPdf(String warehouseId, String userId) {
        InventoryReport report = generateLowStockAlertReport(warehouseId, userId);
        return pdfGenerationService.generateLowStockAlertReportPdf(report);
    }

    /**
     * Génère un PDF à partir d'un rapport existant
     */
    public byte[] generatePdfFromExistingReport(String reportId) {
        InventoryReport report = getReportById(reportId);

        switch (report.getReportType()) {
            case STOCK_STATUS:
                return pdfGenerationService.generateStockStatusReportPdf(report);
            case STOCK_MOVEMENTS:
                return pdfGenerationService.generateMovementsReportPdf(report);
            case LOW_STOCK_ALERT:
                return pdfGenerationService.generateLowStockAlertReportPdf(report);
            default:
                throw new RuntimeException("Type de rapport non supporté pour la génération PDF: " + report.getReportType());
        }
    }
}
