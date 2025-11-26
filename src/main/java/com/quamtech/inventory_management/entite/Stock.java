package com.quamtech.inventory_management.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "stocks")
@CompoundIndex(name = "product_warehouse_location_idx",
        def = "{'productId': 1, 'warehouseId': 1, 'locationId': 1}")
public class Stock {
    @Id
    private String id;


    private String productId;
    private String warehouseId;
    private String locationId; // Emplacement spécifique (optionnel)
    private Integer quantity;
    private Integer reservedQuantity = 0; // Quantité réservée (commandes en attente)
    private Integer availableQuantity; // Calculé: quantity - reservedQuantity
    // Informations pour suivi par lot (optionnel)
    private String lotNumber;
    private String expiryDate;
    // Informations pour suivi par numéro de série (optionnel)
    private String serialNumber;
    //status numero de serie
    private String serialNumberStatus; // AVAILABLE, SOLD, RETURNED, DEFECTIVE

    private LocalDateTime lastUpdated;
    private String lastUpdatedBy;
}
