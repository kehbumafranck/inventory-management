package com.quamtech.inventory_management.repository;

import com.quamtech.inventory_management.entite.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends MongoRepository<Stock,String> {
    Optional<Stock> findByProductIdAndWarehouseIdAndLocationId(
            String productId, String warehouseId, String locationId);

    List<Stock> findByProductId(String productId);
    List<Stock> findByWarehouseId(String warehouseId);
    List<Stock> findByLocationId(String locationId);
    List<Stock> findByLotNumber(String lotNumber);
    List<Stock> findBySerialNumber(String serialNumber);
    List<Stock> findBySerialNumberStatus(String status);

    List<Stock> findByProductIdAndWarehouseId(String productId, String warehouseId);

    @Query("{ 'quantity' : { $gt: 0 } }")
    List<Stock> findAllWithQuantity();

    @Query("{ 'productId': ?0, 'quantity': { $gt: 0 } }")
    List<Stock> findByProductIdWithQuantity(String productId);

    boolean existsByproductId(String productId);
}
