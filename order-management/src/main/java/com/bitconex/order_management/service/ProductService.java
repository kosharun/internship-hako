package com.bitconex.order_management.service;

import com.bitconex.order_management.repository.ProductRepository;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDTO createProduct(@Valid ProductRequestDTO productRequestDTO) {

    }
}
