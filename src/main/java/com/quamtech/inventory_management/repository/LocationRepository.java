package com.quamtech.inventory_management.repository;

import com.quamtech.inventory_management.entite.Location;
import org.springframework.data.mongodb.repository.MongoRepository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {
    Optional<Location> findByCode(String code);
    List<Location> findByWarehouseId(String warehouseId);
    List<Location> findByWarehouseIdAndActiveTrue(String warehouseId);
    List<Location> findByType(String type);
}
