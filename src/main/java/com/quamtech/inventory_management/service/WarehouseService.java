package com.quamtech.inventory_management.service;

import com.quamtech.inventory_management.entite.Warehouse;
import com.quamtech.inventory_management.payload.ApiResponse;
import com.quamtech.inventory_management.payload.request.WarehouseRequest;
import com.quamtech.inventory_management.repository.WarehouseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public Warehouse createWarehouse( WarehouseRequest warehouseRequest) {
        Warehouse createWarehouse1=Warehouse.builder()
                .name(warehouseRequest.getName())
                .code(warehouseRequest.getCode())
                .address(warehouseRequest.getAddress())
                .city(warehouseRequest.getCity())
                .postalCode(warehouseRequest.getPostalCode())
                .country(warehouseRequest.getCountry())
                .contactEmail(warehouseRequest.getContactEmail())
                .contactPhone(warehouseRequest.getContactPhone())
                .manager(warehouseRequest.getManager())
                .capacity(warehouseRequest.getCapacity())
                .active(true)
                .createdAt(warehouseRequest.getCreatedAt())
                .build();
        return warehouseRepository.save(createWarehouse1);
    }

    public Warehouse updateWarehouse(String id,  WarehouseRequest warehouseRequest) {
        Warehouse existing = getWarehouseById(id);
        existing.setName(warehouseRequest.getName());
        existing.setCode(warehouseRequest.getCode());
        existing.setAddress(warehouseRequest.getAddress());
        existing.setCity(warehouseRequest.getCity());
        existing.setPostalCode(warehouseRequest.getPostalCode());
        existing.setCountry(warehouseRequest.getCountry());
        existing.setContactEmail(warehouseRequest.getContactEmail());
        existing.setContactPhone(warehouseRequest.getContactPhone());
        existing.setManager(warehouseRequest.getManager());
        existing.setCapacity(warehouseRequest.getCapacity());
        existing.setActive(true);
        existing.setUpdatedAt(LocalDateTime.now());
        return warehouseRepository.save(existing);
    }
    public Warehouse getWarehouseById(String id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé avec l'ID: " + id));
    }

    public List<Warehouse> getAllWarehouses() {
        List<Warehouse>warehouseList=warehouseRepository.findAll();
        if (warehouseList.isEmpty()){
            throw  new RuntimeException("liste dentrepot vide");
        }
        return warehouseRepository.findAll();
    }

    public List<Warehouse> getActiveWarehouses() {
        return warehouseRepository.findByActiveTrue();
    }

    public void deleteWarehouse(String id) {
        warehouseRepository.deleteById(id);
    }

}
