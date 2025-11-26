package com.quamtech.inventory_management.payload.request;

import com.quamtech.inventory_management.entite.InventoryReport;
import com.quamtech.inventory_management.enumeration.ReportType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportRequest {
    private String title;
    private ReportType reportType;

    private LocalDateTime generatedAt;
    private String generatedBy;

    private String warehouseId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Pour inventaires physiques
    private String physicalInventoryStatus; // PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
    private LocalDateTime physicalInventoryDate;
    private String assignedTo;

    private List<InventoryReport.ReportItem> items;
    private InventoryReport.ReportSummary summary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportItem {
        private String productId;
        private String productName;
        private String sku;
        private String warehouseName;
        private String locationCode;
        private String lotNumber;
        private String serialNumber;

        private Integer quantity;
        private Integer reservedQuantity;
        private Integer availableQuantity;

        // Pour inventaire physique
        private Integer systemQuantity;
        private Integer countedQuantity;
        private Integer variance; // Écart
        private String countedBy;
        private LocalDateTime countedAt;
        private boolean reconciled;

        private Double unitPrice;
        private Double totalValue;

        private Integer movements;
        private Integer daysOfStock; // Nombre de jours de stock
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportSummary {
        private Integer totalProducts;
        private Integer totalQuantity;
        private Integer totalReserved;
        private Integer totalAvailable;
        private Double totalValue;
        private Integer totalMovements;

        // Alertes
        private Integer lowStockProducts;
        private Integer outOfStockProducts;
        private Integer needsReorderProducts;

        // Pour inventaire physique
        private Integer totalVariance;
        private Double varianceValue;
        private Integer itemsCounted;
        private Integer itemsReconciled;
    }
}
