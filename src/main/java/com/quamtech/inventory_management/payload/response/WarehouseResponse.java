package com.quamtech.inventory_management.payload.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class WarehouseResponse {
    @NotBlank(message = "Le nom de l'entrepôt est obligatoire")
    private String name;

    @NotBlank(message = "Le code de l'entrepôt est obligatoire")
    private String code;
    @NotBlank(message = "L'address de l'entrepôt est obligatoire")
    private String address;
    @NotBlank(message = "La ville de l'entrepôt est obligatoire")
    private String city;
    @NotBlank(message = "Le code postal l'entrepôt est obligatoire")
    private String postalCode;
    @NotBlank(message = "Le  pays l'entrepôt est obligatoire")
    private String country;

    @Email(message = "Email invalide")
    private String contactEmail;
    @NotBlank(message = "Le numero  de l'entrepôt est obligatoire")
    private String contactPhone;
    @NotBlank(message = "Le nom du gerant de l'entrepôt est obligatoire")
    private String manager;
    private boolean active = true;
    private Double capacity; // capacité en m³
    private LocalDateTime updatedAt;
}
