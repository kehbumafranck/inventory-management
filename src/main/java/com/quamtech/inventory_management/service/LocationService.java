package com.quamtech.inventory_management.service;

import com.quamtech.inventory_management.entite.Location;
import com.quamtech.inventory_management.exception.InventoryException;
import com.quamtech.inventory_management.payload.request.LocationRequest;
import com.quamtech.inventory_management.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    public Location createEmplacement(LocationRequest locationRequest) {
        Location createEmplacement1=Location.builder()
                .code(locationRequest.getCode())
                .warehouseId(locationRequest.getWarehouseId())
                .aisle(locationRequest.getAisle())
                .bin(locationRequest.getBin())
                .rack(locationRequest.getRack())
                .shelf(locationRequest.getShelf())
                .type(locationRequest.getType())
                .capacity(locationRequest.getCapacity())
                .active(locationRequest.isActive())
                .build();
        return locationRepository.save(createEmplacement1);
    }
    public Location updateLocation(String id, LocationRequest locationRequest) throws InventoryException {
        Location existing = getLocationById(id);
        existing.setCode(locationRequest.getCode());
        existing.setWarehouseId(locationRequest.getWarehouseId());
        existing.setAisle(locationRequest.getAisle());
        existing.setRack(locationRequest.getRack());
        existing.setShelf(locationRequest.getShelf());
        existing.setBin(locationRequest.getBin());
        existing.setType(locationRequest.getType());
        existing.setCapacity(locationRequest.getCapacity());
        existing.setActive(locationRequest.isActive());
        return locationRepository.save(existing);
    }

    public Location getLocationById(String id) throws InventoryException {
        return locationRepository.findById(id)
                .orElseThrow(() -> new InventoryException("Emplacement non trouvé avec l'ID: " + id));
    }

    public List<Location> getAllLocations() throws InventoryException {
        List<Location> locationList=locationRepository.findAll();
        if (locationList.isEmpty()){
            throw new InventoryException("liste vide");
        }
        return locationRepository.findAll();
    }

    public List<Location> getLocationsByWarehouse(String warehouseId) throws InventoryException {
        List<Location> locationList=locationRepository.findByWarehouseIdAndActiveTrue(warehouseId);
        if (locationList.isEmpty()){
            throw new InventoryException("l'emplacement avec l'id"+warehouseId+"n'existe pas");
        }
        return locationList ;
    }

    public void deleteLocation(String id) throws InventoryException {
        if (!locationRepository.existsById(id)){
            throw new InventoryException("cet id n'existe par");
        }
        locationRepository.deleteById(id);
    }

}
