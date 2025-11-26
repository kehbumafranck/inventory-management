package com.quamtech.inventory_management.entite;

import com.quamtech.inventory_management.enumeration.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inventory_reports")
public class InventoryReport {
    @Id
    private String id;

    private String title;
    private ReportType reportType;

    private LocalDate generatedAt;
    private String generatedBy;//utilisateur qui a declanché

    private String warehouseId;
    private LocalDateTime startDate;//Période couverte par le rapport.
    private LocalDateTime endDate;

    // Pour inventaires physiques
    private String physicalInventoryStatus; // PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
    private LocalDateTime physicalInventoryDate;
    private String assignedTo;

    private List<ReportItem> items;
    private ReportSummary summary;

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

