package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.OrderItemDTO;
import com.bitconex.order_management.dto.ProductRequestDTO;
import com.bitconex.order_management.entity.Catalog;
import com.bitconex.order_management.entity.OrderItem;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.CatalogRepository;
import com.bitconex.order_management.repository.OrderItemRepository;
import com.bitconex.order_management.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.bitconex.order_management.utils.ConsoleUtil.printSuccess;
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

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private ProductService productService;

    private Catalog catalog;
    private Product product;
    private ProductRequestDTO productRequestDTO;

    @BeforeEach
    void setUp() {
        catalog = Catalog.builder()
                .name("TestCatalog")
                .build();

        product = Product.builder()
                .catalog(catalog)
                .name("Mock Product Name")
                .description("This is a mock description for testing.")
                .price(99.99)
                .datePublished(LocalDate.of(2024, 2, 1))
                .availableUntil(LocalDate.of(2025, 2, 1))
                .stockQuantity(50)
                .build();

        productRequestDTO = ProductRequestDTO.builder()
                .name("Mock Product Name")
                .description("This is a mock description for testing.")
                .price(99.99)
                .datePublished(LocalDate.of(2024, 2, 1))
                .availableUntil(LocalDate.of(2025, 2, 1))
                .stockQuantity(50)
                .build();
    }

    // CREATE TESTS

    @Test
    @DisplayName("Should create product")
    void testCreateProduct_ShouldCreateProduct() {
        when(catalogRepository.findFirstByOrderByCatalogId()).thenReturn(Optional.ofNullable(catalog));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product productFinal = productService.createProduct(productRequestDTO);

        assertThat(productFinal).isNotNull();
        assertThat(productFinal.getName()).isEqualTo("Mock Product Name");
    }

    @Test
    @DisplayName("Should throw exception if catalog is not found")
    void testCreateProduct_ShouldThrowException_WhenCatalogNotFound() {
        when(catalogRepository.findFirstByOrderByCatalogId()).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.createProduct(productRequestDTO));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should fail when name is missing")
    void testCreateProduct_ShouldFail_WhenNameIsMissing() {
        ProductRequestDTO invalidProductRequestDTO = ProductRequestDTO.builder()
                .description("Mock Description")
                .price(99.99)
                .datePublished(LocalDate.of(2024, 2, 1))
                .availableUntil(LocalDate.of(2025, 2, 1))
                .stockQuantity(50)
                .build();

        assertThrows(Exception.class, () -> productService.createProduct(invalidProductRequestDTO));
    }

    // GET TESTS

    @Test
    @DisplayName("Should retrieve all products")
    void testGetAllProducts_ShouldReturnAllProducts() {
        Product product2 = Product.builder()
                .name("Mock Product Name2")
                .description("This is a mock description for testing 2.")
                .price(99.99)
                .datePublished(LocalDate.of(2024, 2, 1))
                .availableUntil(LocalDate.of(2025, 2, 1))
                .stockQuantity(50)
                .build();

        List<Product> products = List.of(product, product2);

        when(productRepository.findAll()).thenReturn(products);

        List<Product> productsFinal = productService.getAllProducts();

        assertThat(productsFinal).isNotEmpty();
    }

    @Test
    @DisplayName("Should retrieve a product by id")
    void testGetProductById_ShouldReturnAProduct() {
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        Product productFinal = productService.getProductById(product.getProductId());

        assertThat(productFinal).isNotNull();
        assertThat(productFinal.getName()).isEqualTo("Mock Product Name");
    }

    @Test
    @DisplayName("Should throw exception when no products")
    void testGetAllProducts_ShouldThrowException_WhenNoProducts() {
        when(productRepository.findAll()).thenReturn(List.of());

        assertThrows(Exception.class, () -> productService.getAllProducts());
    }

    @Test
    @DisplayName("Should throw exception when no product")
    void testGetProductById_ShouldThrowException_WhenNoProduct() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> productService.getProductById(2L));
    }

    // REMOVE TESTS

    @Test
    @DisplayName("Should remove a product")
    void testRemoveProduct_ShouldRemoveProduct() {
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        productService.removeProduct(product.getProductId());

        verify(orderItemRepository, times(1)).deleteByProduct(product);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("Should throw exception when no product")
    void testRemoveProduct_ShouldThrowException_WhenNoProduct() {
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> productService.removeProduct(product.getProductId()));
    }

    // FIND TESTS

    @Test
    @DisplayName("Should return list of OrderItemDTO when order items exist for a product")
    void testFindOrderItemsRelatedToProduct_ShouldReturnOrderItemDTOList() {
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(2)
                .build();
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(product.getProductId())
                .quantity(2)
                .build();

        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(orderItemRepository.findAllByProduct(product)).thenReturn(List.of(orderItem));
        when(dtoMapper.mapToDTO(orderItem)).thenReturn(orderItemDTO);

        List<OrderItemDTO> result = productService.findOrderItemsRelatedToProduct(product.getProductId());

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getProductId()).isEqualTo(product.getProductId());
        assertThat(result.get(0).getQuantity()).isEqualTo(2);

        verify(productRepository, times(1)).findById(product.getProductId());
        verify(orderItemRepository, times(1)).findAllByProduct(product);
        verify(dtoMapper, times(1)).mapToDTO(orderItem);
    }

    @Test
    @DisplayName("Should throw exception when no order items are found for a product")
    void testFindOrderItemsRelatedToProduct_ShouldThrowException_WhenNoOrderItemsFound() {
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(orderItemRepository.findAllByProduct(product)).thenReturn(List.of());

        assertThrows(Exception.class, () -> productService.findOrderItemsRelatedToProduct(product.getProductId()));

        verify(productRepository, times(1)).findById(product.getProductId());
        verify(orderItemRepository, times(1)).findAllByProduct(product);
    }


    @Test
    @DisplayName("Should retrieve all available products")
    void testGetAllAvailableProducts_ShouldReturnAvailableProducts() {
        // Arrange: create a product that is available (its availableUntil is in the future)
        Product availableProduct = Product.builder()
                .catalog(catalog)
                .name("Available Product")
                .description("Test available product")
                .price(49.99)
                .datePublished(LocalDate.of(2023, 1, 1))
                .availableUntil(LocalDate.now().plusDays(10))
                .stockQuantity(20)
                .build();
        availableProduct.setAvailable(true);

        // checkProductAvailability() is called internally so simulate the outcome:
        when(productRepository.findAll()).thenReturn(List.of(availableProduct));
        when(productRepository.findAllByAvailableTrue()).thenReturn(List.of(availableProduct));

        // Act:
        List<Product> result = productService.getAllAvailableProducts();

        // Assert:
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo("Available Product");
    }

    @Test
    @DisplayName("Should throw exception when no available products are found")
    void testGetAllAvailableProducts_ShouldThrowException_WhenNoAvailableProducts() {
        // Arrange: simulate empty list for available products
        when(productRepository.findAll()).thenReturn(List.of());
        when(productRepository.findAllByAvailableTrue()).thenReturn(List.of());

        // Act & Assert:
        assertThrows(RuntimeException.class, () -> productService.getAllAvailableProducts());
    }


    @Test
    @DisplayName("Should set product availability")
    void testSetProductAvailability_ShouldSetAvailability() {
        // Arrange:
        product.setAvailable(false);
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        // Act: set availability to true
        productService.setProductAvailability(product.getProductId(), true);

        // Assert:
        verify(productRepository, times(1)).save(product);
        assertThat(product.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when setting availability for non-existing product")
    void testSetProductAvailability_ShouldThrowException_WhenProductNotFound() {
        // Arrange:
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.empty());

        // Act & Assert:
        assertThrows(RuntimeException.class, () -> productService.setProductAvailability(product.getProductId(), true));
    }


    @Test
    @DisplayName("Should reduce stock when sufficient quantity is available")
    void testReduceStock_ShouldReduceQuantity() {
        // Arrange:
        product.setStockQuantity(50);
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        // Act:
        int remaining = productService.reduceStock(product.getProductId(), 20);

        // Assert:
        assertThat(remaining).isEqualTo(30);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Should throw exception when reducing stock beyond available quantity")
    void testReduceStock_ShouldThrowException_WhenInsufficientStock() {
        // Arrange:
        product.setStockQuantity(10);
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        // Act & Assert:
        assertThrows(RuntimeException.class, () -> productService.reduceStock(product.getProductId(), 20));
    }

    @Test
    @DisplayName("Should update product quantity and set available to true if quantity > 0 and product not available")
    void testUpdateProductQuantity_ShouldUpdateQuantityAndSetAvailable() {
        // Arrange: product not available and quantity is zero
        product.setAvailable(false);
        product.setStockQuantity(0);
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        // Act:
        productService.updateProductQuantity(product.getProductId(), 30);

        // Assert:
        assertThat(product.getStockQuantity()).isEqualTo(30);
        assertThat(product.isAvailable()).isTrue();
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Should update product quantity without changing availability if already available")
    void testUpdateProductQuantity_ShouldUpdateQuantityWithoutChangingAvailability() {
        // Arrange: product already available
        product.setAvailable(true);
        product.setStockQuantity(50);
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        // Act:
        productService.updateProductQuantity(product.getProductId(), 40);

        // Assert:
        assertThat(product.getStockQuantity()).isEqualTo(40);
        assertThat(product.isAvailable()).isTrue();
        verify(productRepository, times(1)).save(product);
    }

}
