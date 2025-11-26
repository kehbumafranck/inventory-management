package com.quamtech.inventory_management.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {
    @Id
    private String id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    private String name;

    private String description;

    @NotBlank(message = "Le SKU est obligatoire")
    private String sku;

    private String category;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix doit être positif")
    private Double unitPrice;

    private String unit; // pièce, kg, litre, etc.

    // Seuils pour alertes
    @NotNull(message = "Le seuil de réapprovisionnement est obligatoire")
    @Min(value = 0, message = "Le seuil doit être positif")
    private Integer reorderLevel; // Seuil de réapprovisionnement

    @NotNull(message = "Le seuil minimum est obligatoire")
    @Min(value = 0, message = "Le seuil minimum doit être positif")
    private Integer minimumThreshold; // Alerte stock critique

    private Integer optimalStock; // Stock optimal

    // Options de suivi
    private boolean trackByLot = false; // Si true, suivre par lot
    private boolean trackBySerialNumber = false; // Si true, suivre par N° série

    // Attributs pour le suivi par lot (si trackByLot = true)
    private String lotNumber; // Numéro de lot
    private String expiryDate; // Date d'expiration (format ISO String)
    private String manufacturingDate; // Date de fabrication
    private String supplier; // Fournisseur

    // Attributs pour le suivi par numéro de série (si trackBySerialNumber = true)
    private String serialNumber; // Numéro de série individuel

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active = true;
}
