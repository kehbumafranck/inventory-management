package com.quamtech.inventory_management.enumeration;

public enum AlertLevel {
    CRITICAL,   // Stock en dessous du seuil minimum
    WARNING,    // Stock faible mais au-dessus du minimum
    REORDER     // Stock au niveau de réapprovisionnement
}
