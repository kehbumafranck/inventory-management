package com.quamtech.inventory_management.enumeration;

public enum MovementType {
    // Achats / Ventes
    PURCHASE_RECEIPT,    // Réception achat
    SALES_SHIPMENT,      // Expédition vente

    // Retours
    CUSTOMER_RETURN,     // Retour client
    SUPPLIER_RETURN,     // Retour fournisseur

    // Transferts
    TRANSFER_OUT,        // Transfert sortant
    TRANSFER_IN,         // Transfert entrant

    // Ajustements
    INVENTORY_ADJUSTMENT,// Ajustement suite à inventaire physique
    MANUAL_ADJUSTMENT,   // Ajustement manuel

    // Production
    PRODUCTION_IN,       // Entrée production
    PRODUCTION_OUT,      // Sortie production

    // Autres
    DAMAGE,              // Casse/Perte
    SAMPLE,              // Échantillon
    THEFT,               // Vol
    EXPIRED              // Produit expiré
}
