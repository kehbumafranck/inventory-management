package com.quamtech.inventory_management.controller;

import com.quamtech.inventory_management.entite.Product;
import com.quamtech.inventory_management.payload.request.ProductRequest;
import com.quamtech.inventory_management.payload.response.ProductResponse;
import com.quamtech.inventory_management.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/createProduct")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest product) {
        return new ResponseEntity<>(productService.createProduct(product), HttpStatus.CREATED);
    }

    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @Valid @RequestBody ProductResponse product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @GetMapping("getProductById/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<Page<Product>> getAllProducts(@RequestParam(required = false) Boolean active, Pageable pageable) {
        if (active != null && active) {
            return ResponseEntity.ok(productService.getActiveProducts(pageable));
        }
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/search Products")
    public ResponseEntity<Page<Product>> searchProducts(@RequestParam String name,Pageable pageable) {
        return ResponseEntity.ok(productService.searchProductsByName(name,pageable));
    }

    @GetMapping("/getProductBySku/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
