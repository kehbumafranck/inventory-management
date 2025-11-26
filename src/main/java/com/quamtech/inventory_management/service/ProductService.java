package com.quamtech.inventory_management.service;

import com.quamtech.inventory_management.entite.Product;
import com.quamtech.inventory_management.payload.request.ProductRequest;
import com.quamtech.inventory_management.payload.response.ProductResponse;
import com.quamtech.inventory_management.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product createProduct (ProductRequest productRequest){
       Product ceateProduit=Product.builder()
               .name(productRequest.getName())
               .description(productRequest.getDescription())
               .sku(productRequest.getSku())
               .category(productRequest.getCategory())
               .lotNumber(productRequest.getLotNumber())
               .unitPrice(productRequest.getUnitPrice())
               .unit(productRequest.getUnit())
               .reorderLevel(productRequest.getReorderLevel())
               .minimumThreshold(productRequest.getMinimumThreshold())
               .optimalStock(productRequest.getOptimalStock())
               .serialNumber(productRequest.getSerialNumber())
               .manufacturingDate(productRequest.getManufacturingDate())
               .supplier(productRequest.getSupplier())
               .reorderLevel(productRequest.getReorderLevel())
               .trackBySerialNumber(productRequest.isTrackBySerialNumber())
               .trackByLot(productRequest.isTrackByLot())
               .active(productRequest.isActive())
               .createdAt(LocalDateTime.now())
               .build();
       return  productRepository.save(ceateProduit);
    }
    public Product updateProduct(String id,  ProductResponse product) {
        Product existing = getProductById(id);
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setSku(product.getSku());
        existing.setCategory(product.getCategory());
        existing.setUnitPrice(product.getUnitPrice());
        existing.setUnit(product.getUnit());
        existing.setReorderLevel(product.getReorderLevel());
        existing.setMinimumThreshold(product.getMinimumThreshold());
        existing.setOptimalStock(product.getOptimalStock());
        existing.setTrackByLot(product.isTrackByLot());
        existing.setTrackBySerialNumber(product.isTrackBySerialNumber());
        existing.setLotNumber(product.getLotNumber());
        existing.setExpiryDate(product.getExpiryDate());
        existing.setManufacturingDate(product.getManufacturingDate());
        existing.setSupplier(product.getSupplier());
        existing.setSerialNumber(product.getSerialNumber());
        existing.setActive(product.isActive());
        existing.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(existing);
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> getActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
    public Page<Product> searchProductsByName(String name,Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name,pageable);
    }
    public  Page<Product>getProductByTrackByLotTrue(Pageable pageable){
        return productRepository.findByTrackByLotTrue(pageable);
    }
    public Page<Product>getproductByTrackBySerialNumberTrue(Pageable pageable){
        return productRepository.findByTrackBySerialNumberTrue(pageable);
    }
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    public Page<Product> getProductsByLot(String lotNumber,Pageable pageable) {
        return productRepository.findByLotNumber(lotNumber,pageable);
    }

    public Page<Product> getProductsBySerialNumber(String serialNumber,Pageable pageable) {
        return productRepository.findBySerialNumber(serialNumber,pageable);
    }
}
