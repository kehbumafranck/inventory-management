package com.quamtech.inventory_management.payload.response;

import com.quamtech.inventory_management.enumeration.MovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMouvementResponse {
    @NotBlank(message = "L'ID du produit est obligatoire")
    private String productId;

    @NotBlank(message = "L'ID de l'entrepôt est obligatoire")
    private String warehouseId;

    private String locationId;

    @NotNull(message = "Le type de mouvement est obligatoire")
    private MovementType type;

    @NotNull(message = "La quantité est obligatoire")
    private Integer quantity;

    // Pour les transferts
    private String destinationWarehouseId;
    private String destinationLocationId;

    // Traçabilité du document
    private String reference; // Bon de commande, facture, etc.
    private String reason;
    private String notes;

    // Coûts
    private Double unitCost;
    private Double totalCost;

    // Informations de lot/série
    private String lotNumber;
    private String serialNumber;

    // Inventaire physique
    private String physicalInventoryId; // Si lié à un inventaire
    private Integer countedQuantity; // Quantité comptée
    private Integer systemQuantity; // Quantité système
    private Integer variance; // Écart

    // Audit complet
    @NotNull(message = "La date du mouvement est obligatoire")
    private LocalDateTime movementDate;

    @NotBlank(message = "L'utilisateur est obligatoire")
    private String performedBy; // Qui a effectué le mouvement

    private String deviceInfo; // Info du scanner/terminal
}
