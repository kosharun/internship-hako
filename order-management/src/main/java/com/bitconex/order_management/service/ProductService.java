package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.OrderItemDTO;
import com.bitconex.order_management.dto.ProductRequestDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.dto.UserRequestDTO;
import com.bitconex.order_management.entity.Catalog;
import com.bitconex.order_management.entity.OrderItem;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.entity.User;
import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.CatalogRepository;
import com.bitconex.order_management.repository.OrderItemRepository;
import com.bitconex.order_management.repository.ProductRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.bitconex.order_management.utils.ConsoleUtil.print;
import static com.bitconex.order_management.utils.ConsoleUtil.printSuccess;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CatalogRepository catalogRepository;
    private final OrderItemRepository orderItemRepository;
    private final DTOMapper dtoMapper;

    public ProductService(ProductRepository productRepository, CatalogRepository catalogRepository, OrderItemRepository orderItemRepository, DTOMapper dtoMapper) {
        this.productRepository = productRepository;
        this.catalogRepository = catalogRepository;
        this.orderItemRepository = orderItemRepository;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
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

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if(products.isEmpty()) {
            throw new RuntimeException("Products not found!");
        }

        return products;
    }

    public void checkProductAvailability() {
        List<Product> products = productRepository.findAll();
        for(Product product : products) {
            if(product.getAvailableUntil().isBefore(LocalDate.now())) {
                product.setAvailable(false);
                productRepository.save(product);
            }
        }
    }

    public List<Product> getAllAvailableProducts() {
        checkProductAvailability();
        List<Product> products = productRepository.findAllByAvailableTrue();
        if(products.isEmpty()) {
            throw new RuntimeException("No available products found!");
        }

        return products;
    }

    public void setProductAvailability(Long productId, boolean available) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setAvailable(available);
        productRepository.save(product);
    }

    public Product getProductById(Long Id)  {
        return productRepository.findById(Id).orElseThrow(() -> new RuntimeException("Cannot find product"));
    }

    @Transactional
    public void removeProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find product!"));

        orderItemRepository.deleteByProduct(product);

        productRepository.delete(product);
        printSuccess("Successfully removed product: " + product.getName());
    }

    public List<OrderItemDTO> findOrderItemsRelatedToProduct(Long Id) {
        Optional<Product> product = productRepository.findById(Id);
        List<OrderItem> orderItems = orderItemRepository.findAllByProduct(product.get());

        if(orderItems.isEmpty()) {
            throw new RuntimeException("No order items found for product: " + product.get().getName());
        }

        return orderItems.stream().map(dtoMapper::mapToDTO).toList();
    }

    public int reduceStock (Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock for product: " + product.getName());
        }

        int quantityLeft = product.getStockQuantity() - quantity;

        product.setStockQuantity(quantityLeft);
        productRepository.save(product);
        return quantityLeft;
    }

    public void updateProductQuantity(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStockQuantity(quantity);
        if(!product.isAvailable() && quantity > 0) {
            product.setAvailable(true);
        }
        productRepository.save(product);
    }





}
