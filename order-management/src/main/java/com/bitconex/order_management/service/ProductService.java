package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.ProductRequestDTO;
import com.bitconex.order_management.dto.UserRequestDTO;
import com.bitconex.order_management.entity.Catalog;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.repository.CatalogRepository;
import com.bitconex.order_management.repository.ProductRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.bitconex.order_management.utils.ConsoleUtil.printSuccess;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CatalogRepository catalogRepository;

    public ProductService(ProductRepository productRepository, CatalogRepository catalogRepository) {
        this.productRepository = productRepository;
        this.catalogRepository = catalogRepository;
    }

    public Product createProduct(ProductRequestDTO productRequestDTO) {

        Catalog catalog = catalogRepository.findFirstByOrderByCatalogId().orElseThrow(() -> new RuntimeException("Default catalog not found!"));

        Product product = Product.builder()
                .catalog(catalog)
                .name(productRequestDTO.getName())
                .description(productRequestDTO.getDescription())
                .price(productRequestDTO.getPrice())
                .datePublished(productRequestDTO.getDatePublished())
                .availableUntil(productRequestDTO.getAvailableUntil())
                .stockQuantity(productRequestDTO.getStockQuantity())
                .build();

        productRepository.save(product);
        printSuccess("Successfully created product: " + productRequestDTO.getName());
        return product;
    }
}
