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
    private final OrderItemService orderItemService;
    private final DTOMapper dtoMapper;

    public ProductService(ProductRepository productRepository, CatalogRepository catalogRepository, OrderItemRepository orderItemRepository, @Lazy OrderItemService orderItemService, DTOMapper dtoMapper) {
        this.productRepository = productRepository;
        this.catalogRepository = catalogRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderItemService = orderItemService;
        this.dtoMapper = dtoMapper;
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

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if(products.isEmpty()) {
            throw new RuntimeException("Products not found!");
        }

        return products;
    }

    public Product getProductById(Long Id)  {
        return productRepository.findById(Id).orElseThrow(() -> new RuntimeException("Cannot find product"));
    }

    @Transactional
    public void removeProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find product!"));

        orderItemRepository.deleteByProduct(product); // Ovo mora postojati u repository-ju

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





}
