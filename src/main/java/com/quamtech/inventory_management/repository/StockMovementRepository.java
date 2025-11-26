package com.quamtech.inventory_management.repository;

import com.quamtech.inventory_management.entite.StockMovement;
import com.quamtech.inventory_management.enumeration.MovementType;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends MongoRepository<StockMovement, String> {
    List<StockMovement> findByProductId(String productId);
    List<StockMovement> findByWarehouseId(String warehouseId);
    List<StockMovement> findByLocationId(String locationId);
    List<StockMovement> findByType(MovementType type);
    List<StockMovement> findByPerformedBy(String performedBy);
    List<StockMovement> findByReference(String reference);
    List<StockMovement> findByLotNumber(String lotNumber);
    List<StockMovement> findBySerialNumber(String serialNumber);
    List<StockMovement> findByPhysicalInventoryId(String physicalInventoryId);

    List<StockMovement> findByMovementDateBetween(LocalDateTime start, LocalDateTime end);

    List<StockMovement> findByProductIdAndWarehouseIdOrderByMovementDateDesc(
            String productId, String warehouseId);

    List<StockMovement> findByProductIdOrderByMovementDateDesc(String productId);

//    Page<StockMovement> findByProductIdAndWarehouseId(
//            String productId,
//            String warehouseId,
//            Pageable pageable
//    );
//
//    Page<StockMovement> findByProductIdAndWarehouseIdAndMovementType(
//            String productId,
//            String warehouseId,
//            MovementType movementType,
//            Pageable pageable
//    );
//
//    Page<StockMovement> findByProductIdAndWarehouseIdAndMovementDateBetween(
//            String productId,
//            String warehouseId,
//            LocalDateTime start,
//            LocalDateTime end,
//            Pageable pageable
//    );
//
//    Page<StockMovement> findByProductIdAndWarehouseIdAndMovementTypeAndMovementDateBetween(
//            String productId,
//            String warehouseId,
//            MovementType movementType,
//            LocalDateTime start,
//            LocalDateTime end,
//            Pageable pageable
//    );
}
