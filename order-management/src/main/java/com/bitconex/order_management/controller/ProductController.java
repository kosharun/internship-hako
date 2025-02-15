package com.bitconex.order_management.controller;

import com.bitconex.order_management.dto.ProductRequestDTO;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return ResponseEntity.ok(productService.createProduct(productRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeProduct(@PathVariable Long id) {
        productService.removeProduct(id);
        return ResponseEntity.ok("Product deleted successfully!");
    }
}
