package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.ProductRequestDTO;
import com.bitconex.order_management.entity.Catalog;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.CatalogRepository;
import com.bitconex.order_management.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CatalogRepository catalogRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should create product")
    void testCreateProduct_ShouldCreateProduct() {
        Catalog catalog = Catalog.builder()
                .name("TestCatalog")
                .build();
        when(catalogRepository.findFirstByOrderByCatalogId()).thenReturn(Optional.ofNullable(catalog));

        Product product = Product.builder()
                .catalog(catalog) // Use a mock Catalog object
                .name("Mock Product Name")
                .description("This is a mock description for testing.")
                .price(99.99)
                .datePublished(LocalDate.of(2024, 2, 1))  // Example date
                .availableUntil(LocalDate.of(2025, 2, 1))  // Future date
                .stockQuantity(50)  // Example stock
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Create a mock ProductRequestDTO
        ProductRequestDTO productRequestDTO = ProductRequestDTO.builder()
                .name("Mock Product Name")
                .description("This is a mock description for testing.")
                .price(99.99)
                .datePublished(LocalDate.of(2024, 2, 1))
                .availableUntil(LocalDate.of(2025, 2, 1))
                .stockQuantity(50)
                .build();

        //Method calling
        Product productFinal = productService.createProduct(productRequestDTO);

        //Check
        assertThat(productFinal).isNotNull();
        assertThat(productFinal.getName()).isEqualTo("Mock Product Name");

    }

    @Test
    @DisplayName("Should throw exception if catalog is not found")
    void testCreateProduct_ShouldThrowException_WhenCatalogNotFound() {

        when(catalogRepository.findFirstByOrderByCatalogId()).thenReturn(Optional.empty());

        // Create a mock ProductRequestDTO
        ProductRequestDTO productRequestDTO = ProductRequestDTO.builder()
                .name("Mock Product Name")
                .description("This is a mock description for testing.")
                .price(99.99)
                .datePublished(LocalDate.of(2024, 2, 1))
                .availableUntil(LocalDate.of(2025, 2, 1))
                .stockQuantity(50)
                .build();


        //Check
        assertThrows(RuntimeException.class, () -> productService.createProduct(productRequestDTO));

        verify(productRepository, never()).save(any(Product.class));
    }
}
