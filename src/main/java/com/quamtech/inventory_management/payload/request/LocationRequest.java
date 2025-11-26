package com.quamtech.inventory_management.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {
    @NotBlank(message = "Le code de l'emplacement est obligatoire")
    private String code; // Ex: A-01-05 (Allée-Rangée-Niveau)

    @NotBlank(message = "L'ID de l'entrepôt est obligatoire")
    private String warehouseId;

    private String aisle; // Allée
    private String rack;  // Rangée
    private String shelf; // Étagère
    private String bin;   // Casier

    private String type; // PICKING, STORAGE, RECEIVING, SHIPPING

    private Double capacity;
    private boolean active = true;
}
