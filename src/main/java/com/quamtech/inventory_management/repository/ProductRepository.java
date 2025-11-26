package com.quamtech.inventory_management.repository;

import com.quamtech.inventory_management.entite.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findBySku(String sku);
    Page<Product> findByCategory(String category, Pageable pageable);
    Page<Product> findByActiveTrue(Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name,Pageable pageable);
    Page<Product> findByTrackByLotTrue(Pageable pageable);
    Page<Product> findByTrackBySerialNumberTrue(Pageable pageable);
    Page<Product> findByLotNumber(String lotNumber,Pageable pageable);
    Page<Product> findBySerialNumber(String serialNumber,Pageable pageable);
}