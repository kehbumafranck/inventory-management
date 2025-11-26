package com.quamtech.inventory_management.service;

import com.quamtech.inventory_management.entite.Product;
import com.quamtech.inventory_management.entite.Stock;
import com.quamtech.inventory_management.entite.Warehouse;
import com.quamtech.inventory_management.payload.StockAlertInfo;
import com.quamtech.inventory_management.repository.LocationRepository;
import com.quamtech.inventory_management.repository.ProductRepository;
import com.quamtech.inventory_management.repository.StockRepository;
import com.quamtech.inventory_management.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;



    public Stock createOrUpdateStock(Stock stock, String userId) {
        // 1. Calculs préliminaires
        stock.setAvailableQuantity(stock.getQuantity() - stock.getReservedQuantity());
        stock.setLastUpdated(LocalDateTime.now());
        stock.setLastUpdatedBy(userId);

        // 2. Tentative de mise à jour
        try {
            return processStockSave(stock, userId);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Le stock a été modifié par un autre utilisateur. Veuillez rafraîchir et réessayer.");
        }
    }

    private Stock processStockSave(Stock stock, String userId) {
        Optional<Stock> existing = stockRepository.findByProductIdAndWarehouseIdAndLocationId(
                stock.getProductId(),
                stock.getWarehouseId(),
                stock.getLocationId()
        );

        if (existing.isPresent()) {
            Stock existingStock = existing.get();

            // Mise à jour des champs
            existingStock.setQuantity(stock.getQuantity());
            existingStock.setReservedQuantity(stock.getReservedQuantity());
            existingStock.setAvailableQuantity(stock.getAvailableQuantity());
            existingStock.setLotNumber(stock.getLotNumber());
            existingStock.setExpiryDate(stock.getExpiryDate());
            existingStock.setSerialNumber(stock.getSerialNumber());
            existingStock.setSerialNumberStatus(stock.getSerialNumberStatus());

            // Audit
            existingStock.setLastUpdated(stock.getLastUpdated());
            existingStock.setLastUpdatedBy(userId);

            // IMPORTANT : Spring va vérifier ici si existingStock.version == version_en_base
            return stockRepository.save(existingStock);
        }

        return stockRepository.save(stock);
    }

    public Stock getStockById(String id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock non trouvé avec l'ID: " + id));
    }

    public List<Stock> getStocksByProduct(String productId) {
        return stockRepository.findByProductId(productId);
    }

    public List<Stock> getStocksByWarehouse(String warehouseId) {
        return stockRepository.findByWarehouseId(warehouseId);
    }

    public List<Stock> getStocksByLot(String lotNumber) {
        return stockRepository.findByLotNumber(lotNumber);
    }

    public List<Stock> getStocksBySerialNumber(String serialNumber) {
        return stockRepository.findBySerialNumber(serialNumber);
    }
    // recuperé les stocks d’un produit donné dont les numéros de série sont disponibles.
    public List<Stock> getAvailableSerialNumbers(String productId) {
        return stockRepository.findByProductId(productId).stream()
                .filter(s -> "AVAILABLE".equals(s.getSerialNumberStatus()))
                .collect(Collectors.toList());
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }
    //recuperé la quantité  totale d’un produit
    public Integer getTotalQuantityByProduct(String productId) {
        return stockRepository.findByProductId(productId).stream()
                .mapToInt(Stock::getQuantity)
                .sum();
    }
    //recuperé la quantité disponible par un produit
    public Integer getAvailableQuantityByProduct(String productId) {
        return stockRepository.findByProductId(productId).stream()
                .mapToInt(Stock::getAvailableQuantity)
                .sum();
    }
    //faire une reservation de stock
    public void reserveStock(String productId, String warehouseId, String locationId,
                             String lotId, Integer quantity, String userId) {
        Optional<Stock> stockOpt = stockRepository.findByProductIdAndWarehouseIdAndLocationId(
                productId, warehouseId, locationId);

        if (!stockOpt.isPresent()) {
            throw new RuntimeException("Stock non trouvé");
        }

        Stock stock = stockOpt.get();
        if (stock.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Quantité disponible insuffisante");
        }

        stock.setReservedQuantity(stock.getReservedQuantity() + quantity);
        stock.setAvailableQuantity(stock.getQuantity() - stock.getReservedQuantity());
        stock.setLastUpdated(LocalDateTime.now());
        stock.setLastUpdatedBy(userId);
        stockRepository.save(stock);
    }
    //Cette méthode annule une partie d’une réservation et remet cette quantité dans le stock disponible
    public void releaseReservation(String productId, String warehouseId, String locationId,
                                   String lotId, Integer quantity, String userId) {
        Optional<Stock> stockOpt = stockRepository.findByProductIdAndWarehouseIdAndLocationId(
                productId, warehouseId, locationId);

        if (!stockOpt.isPresent()) {
            throw new RuntimeException("Stock non trouvé");
        }

        Stock stock = stockOpt.get();
        stock.setReservedQuantity(Math.max(0, stock.getReservedQuantity() - quantity));
        stock.setAvailableQuantity(stock.getQuantity() - stock.getReservedQuantity());
        stock.setLastUpdated(LocalDateTime.now());
        stock.setLastUpdatedBy(userId);
        stockRepository.save(stock);
    }

    // MÉTHODE pour vérifier les alertes de stock (pas une classe!)
    public List<StockAlertInfo> checkStockAlerts(String warehouseId) {
        List<StockAlertInfo> alerts = new ArrayList<>();

        List<Stock> stocks = warehouseId != null ?
                stockRepository.findByWarehouseId(warehouseId) :
                stockRepository.findAllWithQuantity();

        Map<String, Integer> productTotals = stocks.stream()
                .collect(Collectors.groupingBy(
                        Stock::getProductId,
                        Collectors.summingInt(Stock::getAvailableQuantity)
                ));

        for (Map.Entry<String, Integer> entry : productTotals.entrySet()) {
            String productId = entry.getKey();
            Integer totalAvailable = entry.getValue();

            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) continue;

            StockAlertInfo.AlertLevel alertLevel = null;
            String message = null;

            if (totalAvailable == 0) {
                alertLevel = StockAlertInfo.AlertLevel.CRITICAL;
                message = "RUPTURE DE STOCK - Stock à zéro";
            } else if (totalAvailable < product.getMinimumThreshold()) {
                alertLevel = StockAlertInfo.AlertLevel.CRITICAL;
                message = String.format("STOCK CRITIQUE - Quantité (%d) inférieure au seuil minimum (%d)",
                        totalAvailable, product.getMinimumThreshold());
            } else if (totalAvailable <= product.getReorderLevel()) {
                alertLevel = StockAlertInfo.AlertLevel.REORDER;
                message = String.format("RÉAPPROVISIONNEMENT - Quantité (%d) au niveau de réapprovisionnement (%d)",
                        totalAvailable, product.getReorderLevel());
            }

            if (alertLevel != null) {
                StockAlertInfo alert = new StockAlertInfo();
                alert.setProductId(productId);
                alert.setProductName(product.getName());
                alert.setSku(product.getSku());
                alert.setCurrentQuantity(totalAvailable);
                alert.setAvailableQuantity(totalAvailable);
                alert.setReorderLevel(product.getReorderLevel());
                alert.setMinimumThreshold(product.getMinimumThreshold());
                alert.setAlertLevel(alertLevel);
                alert.setMessage(message);

                if (warehouseId != null) {
                    Warehouse warehouse = warehouseRepository.findById(warehouseId).orElse(null);
                    if (warehouse != null) {
                        alert.setWarehouseId(warehouseId);
                        alert.setWarehouseName(warehouse.getName());
                    }
                }

                alerts.add(alert);
            }
        }

        alerts.sort((a, b) -> {
            int levelCompare = a.getAlertLevel().compareTo(b.getAlertLevel());
            if (levelCompare != 0) return levelCompare;
            return a.getProductName().compareTo(b.getProductName());
        });

        return alerts;
    }
    //recuperé le niveau qu'il faut commandé
    public List<StockAlertInfo> getReorderSuggestions() {
        return checkStockAlerts(null).stream()
                .filter(alert -> alert.getAlertLevel() == StockAlertInfo.AlertLevel.REORDER)
                .collect(Collectors.toList());
    }
    // recuperé le Stock en dessous du seuil minimum
    public List<StockAlertInfo> getCriticalStockAlerts() {
        return checkStockAlerts(null).stream()
                .filter(alert -> alert.getAlertLevel() == StockAlertInfo.AlertLevel.CRITICAL)
                .collect(Collectors.toList());
    }
    // suprimé le stock par id
    public void deleteStock(String id) {
        stockRepository.deleteById(id);
    }
}
