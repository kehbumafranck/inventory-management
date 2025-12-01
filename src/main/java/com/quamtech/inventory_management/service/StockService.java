package com.quamtech.inventory_management.service;

import com.quamtech.inventory_management.entite.Product;
import com.quamtech.inventory_management.entite.Stock;
import com.quamtech.inventory_management.entite.Warehouse;
import com.quamtech.inventory_management.exception.InventoryException;
import com.quamtech.inventory_management.payload.StockAlertInfo;
import com.quamtech.inventory_management.repository.LocationRepository;
import com.quamtech.inventory_management.repository.ProductRepository;
import com.quamtech.inventory_management.repository.StockRepository;
import com.quamtech.inventory_management.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;



    public Stock createOrUpdateStock(Stock req, String userId) throws InventoryException {

        if (req.getQuantity() < 0) {
            throw new InventoryException("quantity ne peut pas être négative.");
        }
        if (req.getReservedQuantity() < 0) {
            throw new InventoryException("reservedQuantity ne peut pas être négative.");
        }
        if (req.getReservedQuantity() > req.getQuantity()) {
            throw new InventoryException("reservedQuantity ne peut pas dépasser quantity.");
        }

        // 2. Recherche d’un stock existant
        Optional<Stock> existingOpt =
                stockRepository.findByProductIdAndWarehouseIdAndLocationId(
                        req.getProductId(),
                        req.getWarehouseId(),
                        req.getLocationId()

                );

        Stock stock;

        if (existingOpt.isPresent()) {
            // 3. Mise à jour
            stock = existingOpt.get();
            stock.setQuantity(req.getQuantity());
            stock.setReservedQuantity(req.getReservedQuantity());
            stock.setAvailableQuantity(req.getQuantity() - req.getReservedQuantity());
            stock.setExpiryDate(req.getExpiryDate());
            stock.setLastUpdated(LocalDateTime.now());
            stock.setLastUpdatedBy(userId);

        } else {
            // 4. Création
            stock = Stock.builder()
                    .productId(req.getProductId())
                    .warehouseId(req.getWarehouseId())
                    .locationId(req.getLocationId())
                    .quantity(req.getQuantity())
                    .reservedQuantity(req.getReservedQuantity())
                    .availableQuantity(req.getQuantity() - req.getReservedQuantity())
                    .lotNumber(req.getLotNumber())
                    .serialNumber(req.getSerialNumber())
                    .expiryDate(req.getExpiryDate())
                    .lastUpdated(LocalDateTime.now())
                    .lastUpdatedBy(userId)
                    .build();
        }

        // 5. Sauvegarde + gestion du verrou optimiste
        try {
            return stockRepository.save(stock);
        } catch (OptimisticLockingFailureException e) {
            throw new InventoryException(
                    "Le stock a été modifié par un autre utilisateur. Veuillez rafraîchir et réessayer."
            );
        }
    }



    public Stock getStockById(String id) throws InventoryException {
        return stockRepository.findById(id)
                .orElseThrow(() -> new InventoryException("Stock non trouvé avec l'ID: " + id));
    }

    public List<Stock> getStocksByProduct(String productId) throws InventoryException {
        List<Stock> stock =stockRepository.findByProductId(productId);
        if (stock.isEmpty()){
            throw  new InventoryException("le stock avec cet id"+productId+"n'existe pas");
        }
        return stock ;
    }

    public List<Stock> getStocksByWarehouse(String warehouseId) throws InventoryException {
        List<Stock> stockList=stockRepository.findByWarehouseId(warehouseId);
        if (stockList.isEmpty()){
            throw new InventoryException("liste de stock de l'entrepot avec l'id="+warehouseId+"n'existe pas");
        }
        return stockRepository.findByWarehouseId(warehouseId);
    }

//    public List<Stock> getStocksByLot(String lotNumber) {
//        List<Stock> stockList=stockRepository.findByLotNumber(lotNumber);
//        if (stockList.isEmpty()){
//            throw new RuntimeException("liste de stock par numero de lot");
//        }
//        return stockRepository.findByLotNumber(lotNumber);
//    }
//
//    public List<Stock> getStocksBySerialNumber(String serialNumber) {
//        List<Stock> stockList=stockRepository.findBySerialNumber(serialNumber);
//        if (stockList.isEmpty()){
//            throw new RuntimeException("liste de stock par numero de seri");
//        }
//        return stockRepository.findBySerialNumber(serialNumber);
//    }
    // recuperé les stocks d’un produit donné dont les numéros de série sont disponibles.
//    public List<Stock> getAvailableSerialNumbers(String productId) {
//        return stockRepository.findByProductId(productId).stream()
//                .filter(s -> "AVAILABLE".equals(s.getSerialNumberStatus()))
//                .collect(Collectors.toList());
//    }

    public List<Stock> getAllStocks() throws InventoryException {
        List<Stock> stockList=stockRepository.findAll();
        if (stockList.isEmpty()){
            throw new InventoryException("liste de stock ");
        }
        return stockRepository.findAll();
    }
    //recuperé la quantité  totale en stock d’un produit
    public Integer getTotalQuantityByProduct(String productId) throws InventoryException {
        if (!stockRepository.existsByproductId(productId)) {
            throw new InventoryException("Le product avec l'id"+productId+"n'existe pas");
        }

            return stockRepository.findByProductId(productId).stream()
                    .map(Stock::getQuantity)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum();

    }
    //recuperé la quantité disponible en stock par un produit
    public Integer getAvailableQuantityByProduct(String productId) throws InventoryException {

        if (!stockRepository.existsByproductId(productId)) {
            throw new InventoryException("Le product avec l'id"+productId+"n'existe pas");
        }

        return stockRepository.findByProductId(productId).stream()
                .map(Stock::getAvailableQuantity)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    //faire une reservation de stock
    public Stock reserveStock(String productId, String warehouseId, String locationId,
                              Integer quantity, String userId) throws InventoryException {
        Optional<Stock> stockOpt = stockRepository.findByProductIdAndWarehouseIdAndLocationId(
                productId, warehouseId, locationId);

        if (!stockOpt.isPresent()) {
            throw new InventoryException("Stock non trouvé");
        }

        Stock stock = stockOpt.get();
        if (stock.getAvailableQuantity() < quantity) {
            throw new InventoryException("Quantité disponible insuffisante");
        }

        stock.setReservedQuantity(stock.getReservedQuantity() + quantity);
        stock.setAvailableQuantity(stock.getQuantity() - stock.getReservedQuantity());
        stock.setLastUpdated(LocalDateTime.now());
        stock.setLastUpdatedBy(userId);

        return stockRepository.save(stock);
    }
    //Cette méthode annule une partie d’une réservation et remet cette quantité dans le stock disponible
    public Stock releaseReservation(String productId, String warehouseId, String locationId,
                                    Integer quantity, String userId) throws InventoryException {
        Optional<Stock> stockOpt = stockRepository.findByProductIdAndWarehouseIdAndLocationId(
                productId, warehouseId, locationId);

        if (!stockOpt.isPresent()) {
            throw new InventoryException("Stock non trouvé");
        }

        Stock stock = stockOpt.get();
        stock.setReservedQuantity(Math.max(0, stock.getReservedQuantity() - quantity));
        stock.setAvailableQuantity(stock.getQuantity() - stock.getReservedQuantity());
        stock.setLastUpdated(LocalDateTime.now());
        stock.setLastUpdatedBy(userId);

        return stockRepository.save(stock);
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

        List<StockAlertInfo> alerts = checkStockAlerts(null);

        if (alerts == null || alerts.isEmpty()) {
            return Collections.emptyList();
        }
        return alerts.stream()
                .filter(alert -> alert.getAlertLevel() == StockAlertInfo.AlertLevel.REORDER)
                .collect(Collectors.toList());
    }
    // recuperé le Stock en dessous du seuil minimum
    public List<StockAlertInfo> getCriticalStockAlerts() {
        List<StockAlertInfo> alerts = checkStockAlerts(null);

        if (alerts == null || alerts.isEmpty()) {
            return Collections.emptyList();
        }
        return alerts.stream()
                .filter(alert -> alert.getAlertLevel() == StockAlertInfo.AlertLevel.CRITICAL)
                .collect(Collectors.toList());
    }
    // suprimé le stock par id
    public void deleteStock(String id) throws InventoryException {
        if (!stockRepository.existsById(id)) {
            throw new InventoryException("Stock avec l'ID " + id + " n'existe pas.");
        }
        stockRepository.deleteById(id);
    }
}
