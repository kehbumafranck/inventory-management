package com.quamtech.inventory_management.repository;

import com.quamtech.inventory_management.entite.InventoryReport;
import com.quamtech.inventory_management.enumeration.ReportType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryReportRepository extends MongoRepository<InventoryReport, String> {
    List<InventoryReport> findByReportType(ReportType reportType);
    List<InventoryReport> findByWarehouseId(String warehouseId);
    List<InventoryReport> findByGeneratedBy(String generatedBy);
    List<InventoryReport> findByPhysicalInventoryStatus(String status);
    List<InventoryReport> findByGeneratedAtBetween(LocalDateTime start, LocalDateTime end);
    List<InventoryReport> findByOrderByGeneratedAtDesc();
}
