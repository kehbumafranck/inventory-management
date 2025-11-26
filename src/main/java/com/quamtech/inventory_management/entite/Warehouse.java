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
@Document(collection = "warehouses")
public class Warehouse {
    @Id
    private String id;


    private String name;
    private String code;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String contactEmail;
    private String contactPhone;
    private String manager;
    private Double capacity; // capacité en m³
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active = true;
}
