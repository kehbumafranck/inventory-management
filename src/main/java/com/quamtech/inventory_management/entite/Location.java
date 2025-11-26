package com.quamtech.inventory_management.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "locations")
public class Location {
    @Id
    private String id;
    private String code; // Ex: A-01-05 (Allée-Rangée-Niveau)
    private String warehouseId;

    private String aisle; // Allée
    private String rack;  // Rangée
    private String shelf; // Étagère
    private String bin;   // Casier

    private String type; // PICKING, STORAGE, RECEIVING, SHIPPING

    private Double capacity;
    private boolean active = true;
}
