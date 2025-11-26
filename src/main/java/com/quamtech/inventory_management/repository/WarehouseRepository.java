package com.quamtech.inventory_management.repository;

import com.quamtech.inventory_management.entite.Warehouse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface WarehouseRepository extends MongoRepository<Warehouse,String> {
    Optional<Warehouse> findByCode(String code);
    List<Warehouse> findByActiveTrue();
    List<Warehouse> findByCity(String city);
}
