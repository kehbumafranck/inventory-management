package com.quamtech.inventory_management.enumeration;

public enum ReportType {
    STOCK_STATUS,           // État des stocks
    STOCK_VALUATION,        // Valorisation du stock
    STOCK_MOVEMENTS,        // Mouvements de stock
    LOW_STOCK_ALERT,        // Alertes stock faible
    REORDER_SUGGESTION,     // Suggestions de réapprovisionnement
    WAREHOUSE_COMPARISON,   // Comparaison entre entrepôts
    PHYSICAL_INVENTORY,     // Inventaire physique
    INVENTORY_VARIANCE,     // Écarts d'inventaire
    LOT_TRACKING,           // Suivi des lots
    SERIAL_NUMBER_TRACKING, // Suivi des numéros de série
    ABC_ANALYSIS            // Analyse ABC
}
