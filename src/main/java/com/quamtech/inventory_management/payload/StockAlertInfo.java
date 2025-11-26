package com.quamtech.inventory_management.payload;
import com.quamtech.inventory_management.enumeration.AlertLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertInfo {

    private String productId;
    private String productName;
    private String sku;
    private String warehouseId;
    private String warehouseName;
    private String locationCode;

    private Integer currentQuantity;
    private Integer availableQuantity;
    private Integer reorderLevel;
    private Integer minimumThreshold;

    private AlertLevel alertLevel; // CRITICAL, WARNING, REORDER
    private String message;
    public enum AlertLevel {
        CRITICAL,   // Stock en dessous du seuil minimum
        WARNING,    // Stock faible mais au-dessus du minimum
        REORDER     // Stock au niveau de réapprovisionnement
    }
}
