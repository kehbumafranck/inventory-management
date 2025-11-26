package com.quamtech.inventory_management.payload.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    @NotBlank(message = "L'ID du produit est obligatoire")
    private String productId;

    @NotBlank(message = "L'ID de l'entrepôt est obligatoire")
    private String warehouseId;
    @NotBlank(message = "L'ID de l'emplacement est obligatoire")
    private String locationId; // Emplacement spécifique (optionnel)

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private Integer quantity;
    @NotNull(message = "La quantité réservée est obligatoire")
    private Integer reservedQuantity = 0; // Quantité réservée (commandes en attente)
    @NotNull(message = "La quantité Calculé est obligatoire")
    private Integer availableQuantity; // Calculé: quantity - reservedQuantity

    // Informations pour suivi par lot (optionnel)
    @NotBlank(message = "Le numero de lot est obligatoire")
    private String lotNumber;
    @NotBlank(message = "La date d'expiration  est obligatoire")
    private String expiryDate;

    // Informations pour suivi par numéro de série (optionnel)
    private String serialNumber;
    private String serialNumberStatus; // AVAILABLE, SOLD, RETURNED, DEFECTIVE

    private LocalDateTime lastUpdated;
    private String lastUpdatedBy;
}
