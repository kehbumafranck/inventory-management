package com.quamtech.inventory_management.controller;

import com.quamtech.inventory_management.entite.Stock;
import com.quamtech.inventory_management.entite.Warehouse;
import com.quamtech.inventory_management.exception.InventoryException;
import com.quamtech.inventory_management.payload.ApiResponse;
import com.quamtech.inventory_management.payload.StockAlertInfo;
import com.quamtech.inventory_management.payload.request.StockRequest;
import com.quamtech.inventory_management.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockController {
    private final StockService stockService;

    @PostMapping("/createOrUpdateStock")
    public ResponseEntity<ApiResponse<Stock>> createOrUpdateStock(@Valid @RequestBody Stock stockRequest,
                                                              @RequestParam String userId) throws InventoryException {
        Stock stock1 =stockService.createOrUpdateStock(stockRequest,userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("entrepot créé avec succès", stock1));
    }

    @GetMapping("/getStockById/{id}")
    public ResponseEntity<ApiResponse<Stock>> getStock(@PathVariable String id) throws InventoryException {
        Stock stock=stockService.getStockById(id);
        return ResponseEntity.ok(ApiResponse.success("l'entrepot  avec l'id"+id+"est", stock));
    }

    @GetMapping("getAllStocks")
    public ResponseEntity<ApiResponse<List<Stock>>> getAllStocks()throws InventoryException {
        return ResponseEntity.ok(ApiResponse.success("la liste est",stockService.getAllStocks()));
    }

    @GetMapping("/getStocksByProduct/{productId}")
    public ResponseEntity<ApiResponse<List<Stock>>> getStocksByProduct(@PathVariable String productId) throws InventoryException {
        List<Stock>stockList=stockService.getStocksByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("list des stock du produit avec l'id"+productId+" sont",stockList));
    }

    @GetMapping("/getStocksByWarehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<List<Stock>>> getStocksByWarehouse(@PathVariable String warehouseId) throws InventoryException {
        List<Stock>stockList=stockService.getStocksByWarehouse(warehouseId);
        return ResponseEntity.ok(ApiResponse.success("la list est",stockList));
    }

    @GetMapping("/getTotalQuantity/{productId}/total")
    public ResponseEntity<ApiResponse<Integer>> getTotalQuantity(@PathVariable String productId)throws InventoryException {
        Integer stock=stockService.getTotalQuantityByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("la totalité de la quantité est",stock));
    }

    @GetMapping("/getAvailableQuantity/{productId}/available")
    public ResponseEntity<ApiResponse<Integer>> getAvailableQuantity(@PathVariable String productId) throws InventoryException {
        Integer stock =stockService.getAvailableQuantityByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("les data sont",stock));
    }

    @PostMapping("/reserveStock")
    public ResponseEntity<ApiResponse<?>> reserveStock(/*@RequestBody Map<String, Object> request */@RequestParam String productId,@RequestParam String warehouseId,@RequestParam String locationId,
                                                                                                    @RequestParam Integer quantity,@RequestParam String userId) throws InventoryException {
//        String productId = (String) request.get("productId");
//        String warehouseId = (String) request.get("warehouseId");
//        String locationId = (String) request.get("locationId");
//        Integer quantity = (Integer) request.get("quantity");
//        String userId = (String) request.get("userId");

        Stock stock= stockService.reserveStock(productId, warehouseId, locationId,  quantity, userId);
        return ResponseEntity.ok(ApiResponse.success("data",stock));
    }

    @PostMapping("/releaseReservation")
    public ResponseEntity<ApiResponse<?>> releaseReservation(@RequestParam String productId,@RequestParam String warehouseId,@RequestParam String locationId,
                                                                                                         @RequestParam  Integer quantity,@RequestParam String userId) throws InventoryException {
       Stock stock= stockService.releaseReservation(productId, warehouseId, locationId,  quantity, userId);
        return ResponseEntity.ok(ApiResponse.success("data",stock));
    }

    // Endpoint pour les alertes de stock (MÉTHODE, pas une classe)
    @GetMapping("/getStockAlerts")
    public ResponseEntity<ApiResponse<List<StockAlertInfo>>> getStockAlerts(
            @RequestParam(required = false) String warehouseId) {
        List<StockAlertInfo> stock=stockService.checkStockAlerts(warehouseId);
        return ResponseEntity.ok(ApiResponse.success("data",stock));
    }
    //Obtenir des alert critique
    @GetMapping("/getCriticalAlerts")
    public ResponseEntity<ApiResponse<List<StockAlertInfo>>> getCriticalAlerts() {
        List<StockAlertInfo> stock=stockService.getCriticalStockAlerts();
        return ResponseEntity.ok(ApiResponse.success("data",stock));
    }
    //Obtenir des suggestions de réapprovisionnement
    @GetMapping("/getReorderSuggestions")
    public ResponseEntity<ApiResponse<List<StockAlertInfo>>> getReorderSuggestions() {
        List<StockAlertInfo> stockAlertInfos=stockService.getReorderSuggestions();
        return ResponseEntity.ok(ApiResponse.success("data",stockAlertInfos));
    }

    @DeleteMapping("/deleteStock/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStock(@PathVariable String id) throws InventoryException {
        stockService.deleteStock(id);
        return ResponseEntity.ok(ApiResponse.success("entrepot supprimé avec succes", null));
    }
}
