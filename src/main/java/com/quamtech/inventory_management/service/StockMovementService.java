package com.quamtech.inventory_management.service;

import com.quamtech.inventory_management.entite.Stock;
import com.quamtech.inventory_management.entite.StockMovement;
import com.quamtech.inventory_management.enumeration.MovementType;
import com.quamtech.inventory_management.exception.InventoryException;
import com.quamtech.inventory_management.repository.StockMovementRepository;
import com.quamtech.inventory_management.utils.DateUtilitie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StockMovementService {
    private final StockMovementRepository movementRepository;
    private final StockService stockService;

    public StockMovement createMovement(StockMovement mouvementRequest, String userId) throws InventoryException {

        LocalDate movementDate;
        try {
            movementDate = DateUtilitie.handleDate(String.valueOf(LocalDate.now()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Date invalide : " + mouvementRequest.getMovementDate());
        }

        StockMovement mouvement1 = StockMovement.builder()
                .productId(mouvementRequest.getProductId())
                .warehouseId(mouvementRequest.getWarehouseId())
                .locationId(mouvementRequest.getLocationId())
                .type(MovementType.valueOf(String.valueOf(mouvementRequest.getType())))
                .quantity(mouvementRequest.getQuantity())
                .destinationWarehouseId(mouvementRequest.getDestinationWarehouseId())
                .destinationLocationId(mouvementRequest.getDestinationLocationId())
                .reference(mouvementRequest.getReference())
                .reason(mouvementRequest.getReason())
                .notes(mouvementRequest.getNotes())
                .unitCost(mouvementRequest.getUnitCost())
                .lotNumber(mouvementRequest.getLotNumber())
                .serialNumber(mouvementRequest.getSerialNumber())
                .physicalInventoryId(mouvementRequest.getPhysicalInventoryId())
                .countedQuantity(mouvementRequest.getCountedQuantity())
                .systemQuantity(mouvementRequest.getSystemQuantity())
                .variance(mouvementRequest.getVariance())
                .performedBy(userId)
                .deviceInfo(mouvementRequest.getDeviceInfo())
                .movementDate(movementDate)   // D A T E  V A L I D É E
                .build();

        if (mouvement1.getUnitCost() != null && mouvement1.getQuantity() != null) {
            mouvement1.setTotalCost(mouvement1.getUnitCost() * mouvement1.getQuantity());
        }

        StockMovement saved = movementRepository.save(mouvement1);
        updateStockFromMovement(saved, userId);

        return saved;
    }




    private void updateStockFromMovement(StockMovement movement, String userId) throws InventoryException {
        Stock stock = stockService.getStocksByProduct(movement.getProductId()).stream()
                .filter(s -> s.getWarehouseId().equals(movement.getWarehouseId()) &&
                        Objects.equals(s.getLocationId(), movement.getLocationId()))
                .findFirst()
                .orElse(new Stock());

        stock.setProductId(movement.getProductId());
        stock.setWarehouseId(movement.getWarehouseId());
        stock.setLocationId(movement.getLocationId());
        stock.setLotNumber(movement.getLotNumber());
        stock.setSerialNumber(movement.getSerialNumber());

        int currentQuantity = stock.getQuantity() != null ? stock.getQuantity() : 0;

        switch (movement.getType()) {
            case PURCHASE_RECEIPT:// Réception achat
            case CUSTOMER_RETURN:// Retour client
            case TRANSFER_IN:// Transfert entrant
            case PRODUCTION_IN:// Entrée production
                stock.setQuantity(currentQuantity + movement.getQuantity());
                if (movement.getSerialNumber() != null) {
                    stock.setSerialNumberStatus("AVAILABLE");
                }
                break;

            case SALES_SHIPMENT:// Expédition vente
            case SUPPLIER_RETURN:// Retour fournisseur
            case TRANSFER_OUT:// Transfert sortant
            case PRODUCTION_OUT:// Sortie production
            case DAMAGE:             // Casse/Perte
            case SAMPLE:              // Échantillon
            case THEFT:                 // Vol
            case EXPIRED:              // Produit expiré
                stock.setQuantity(Math.max(0, currentQuantity - movement.getQuantity()));
                if (movement.getSerialNumber() != null) {
                    stock.setSerialNumberStatus("SOLD");
                }
                break;

            case INVENTORY_ADJUSTMENT:// Ajustement suite à inventaire physique
            case MANUAL_ADJUSTMENT:// Ajustement manuel
                stock.setQuantity(movement.getQuantity());
                break;
        }

        stockService.createOrUpdateStock(stock, userId);

        // Pour les transferts
        if (movement.getType() == MovementType.TRANSFER_OUT &&
                movement.getDestinationWarehouseId() != null) {

            Stock destStock = new Stock();
            destStock.setProductId(movement.getProductId());
            destStock.setWarehouseId(movement.getDestinationWarehouseId());
            destStock.setLocationId(movement.getDestinationLocationId());
            destStock.setLotNumber(movement.getLotNumber());
            destStock.setSerialNumber(movement.getSerialNumber());

            List<Stock> existingDest = stockService.getStocksByProduct(movement.getProductId());
            int destQuantity = existingDest.stream()
                    .filter(s -> s.getWarehouseId().equals(movement.getDestinationWarehouseId()) &&
                            Objects.equals(s.getLocationId(), movement.getDestinationLocationId()))
                    .mapToInt(Stock::getQuantity)
                    .sum();

            destStock.setQuantity(destQuantity + movement.getQuantity());
            stockService.createOrUpdateStock(destStock, userId);
        }
    }

    public StockMovement getMovementById(String id) {
        return movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement non trouvé avec l'ID: " + id));
    }

    public List<StockMovement> getAllMovements() {
        return movementRepository.findAll();
    }

    public List<StockMovement> getMovementsByProduct(String productId) {
        return movementRepository.findByProductIdOrderByMovementDateDesc(productId);
    }

    public List<StockMovement> getMovementsByWarehouse(String warehouseId) {
        return movementRepository.findByWarehouseId(warehouseId);
    }

    public List<StockMovement> getMovementsByType(MovementType type) {
        return movementRepository.findByType(type);
    }

    public List<StockMovement> getMovementsByDateRange(LocalDateTime start, LocalDateTime end) {
        return movementRepository.findByMovementDateBetween(start, end);
    }

    public List<StockMovement> getMovementsByReference(String reference) {
        return movementRepository.findByReference(reference);
    }

    public List<StockMovement> getMovementsByLot(String lotNumber) {
        return movementRepository.findByLotNumber(lotNumber);
    }

    public List<StockMovement> getMovementsBySerialNumber(String serialNumber) {
        return movementRepository.findBySerialNumber(serialNumber);
    }
    //recuperé l’historique des mouvements de stock d’un produit dans un entrepôt donné
    public List<StockMovement> getProductHistory(String productId, String warehouseId) {
        return movementRepository.findByProductIdAndWarehouseIdOrderByMovementDateDesc(
                productId, warehouseId);
    }
    // recupéré l'historique  d’un produit avec filtre par type de mouvement ,par date  par date et type de mouvement
//    public Page<StockMovement>getProductHistory1( String productId, String warehouseId, MovementType movementType, LocalDateTime startDate, LocalDateTime endDate, int page, int size){
//        Pageable pageable = PageRequest.of(page, size, Sort.by("movementDate").descending());
//
//        boolean filterType = movementType != null;
//        boolean filterDate = startDate != null && endDate != null;
//
//        if (filterType && filterDate) {
//            return movementRepository.findByProductIdAndWarehouseIdAndMovementTypeAndMovementDateBetween(
//                    productId, warehouseId, movementType, startDate, endDate, pageable
//            );
//        }
//
//        if (filterType) {
//            return movementRepository.findByProductIdAndWarehouseIdAndMovementType(
//                    productId, warehouseId, movementType, pageable
//            );
//        }
//
//        if (filterDate) {
//            return movementRepository.findByProductIdAndWarehouseIdAndMovementDateBetween(
//                    productId, warehouseId, startDate, endDate, pageable
//            );
//        }
//
//        return movementRepository.findByProductIdAndWarehouseId(
//                productId, warehouseId, pageable
//        );
//    }
}
