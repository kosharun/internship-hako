package com.bitconex.order_management.seed;


import com.bitconex.order_management.entity.*;
import com.bitconex.order_management.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CatalogRepository catalogRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderItemRepository orderItemRepository;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository,
                      ProductRepository productRepository, CatalogRepository catalogRepository,
                      OrderRepository orderRepository, OrderStatusRepository orderStatusRepository,
                      OrderItemRepository orderItemRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.catalogRepository = catalogRepository;
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        /*// Step 1: Delete all existing data
        clearDatabase();

        // Step 2: Seed new data
        seedDatabase();*/
    }

    /**
     * Deletes all data from the database tables.
     */
    private void clearDatabase() {
        // Delete in reverse order to respect foreign key constraints
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        orderStatusRepository.deleteAll();
        productRepository.deleteAll();
        catalogRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        System.out.println("🗑️ All existing data deleted!");
    }

    private void seedDatabase() throws IOException {
        InputStream jsonStream = new ClassPathResource("data.json").getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonData = objectMapper.readTree(jsonStream);

        seedRoles(jsonData.get("roles"));
        seedCatalogs(jsonData.get("catalogs"));
        seedUsers(jsonData.get("users"));
        seedProducts(jsonData.get("products"));
        seedOrderStatuses(jsonData.get("orderStatuses"));
        seedOrders(jsonData.get("orders"));
    }

    private void seedRoles(JsonNode roles) {
        if (roles != null) {
            roles.forEach(roleNode -> {
                String roleName = roleNode.get("name").asText();
                if (roleRepository.findByName(roleName).isEmpty()) {
                    {
                        Role role = new Role();
                        role.setName(roleName);
                        roleRepository.save(role);

                        System.out.println("✅ Role seeded!");
                    }
                }
            });
        }
    }

    private void seedCatalogs(JsonNode catalogs) {
        if(catalogs == null) {
            return;
        }

        catalogs.forEach(catalogNode -> {
            String catalogName = catalogNode.get("name").asText();
            if(catalogRepository.findByName(catalogName).isEmpty()){
                Catalog catalog = new Catalog();
                catalog.setName(catalogName);
                catalogRepository.save(catalog);

                System.out.println("✅ Catalog seeded!");
            }

        });
    }

    private void seedUsers(JsonNode users) {
        if(users == null) {
            return;
        }

        users.forEach(userNode -> {
            String username = userNode.get("username").asText();
            String email = userNode.get("email").asText();
            String password = userNode.get("password").asText();
            String firstName = userNode.get("firstName").asText();
            String lastName = userNode.get("lastName").asText();
            String dateOfBirth = userNode.get("dateOfBirth").asText();
            String roleName = userNode.get("role").asText(); // druga tabela - mora se provjerit jel postoji

            Optional<Role> role = roleRepository.findByName(roleName);
            if (role.isEmpty()) {
                System.out.println("Role " + roleName + "does not exist");
                return;
            }

            if(userRepository.findByUsername(username).isEmpty()){
                User user = new User();

                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setDateOfBirth(LocalDate.parse(dateOfBirth));
                user.setRole(role.get());

                // parsiranje adrese

                JsonNode addressNode = userNode.get("address");
                if(addressNode == null) {
                    return;
                }

                Address address = new Address();
                address.setStreet(addressNode.get("street").asText());
                address.setZipCode(addressNode.get("zipCode").asText());
                address.setPlaceName(addressNode.get("placeName").asText());
                address.setStateName(addressNode.get("stateName").asText());
                user.setAddress(address);

                userRepository.save(user);
                System.out.println("✅ Added user: " + username);
            }
        });
    }

    private void seedProducts(JsonNode products) {
        if (products == null) {
            System.out.println("⚠️ JSON has no section 'products'.");
            return;
        }
        products.forEach(productNode -> {
            String name = productNode.get("name").asText();
            String description = productNode.get("description").asText();
            double price = Double.parseDouble(productNode.get("price").asText());
            String datePublished = productNode.get("datePublished").asText();
            String availableUntil = productNode.get("availableUntil").asText();
            int stockQuantity = Integer.parseInt(productNode.get("stockQuantity").asText());
            String catalogName = productNode.get("catalog").asText();

            if (productRepository.findByName(name).isPresent()) {
                System.out.println("⚠️ Product already exists: " + name);
                return;
            }

            Product product = new Product();

            Optional<Catalog> catalog = catalogRepository.findByName(catalogName);
            if (catalog.isEmpty()) {
                System.out.println("⚠️ Catalog not found for product: " + name + ". Catalog name: " + catalogName);
                return;
            }

            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setDatePublished(LocalDate.parse(datePublished));
            product.setAvailableUntil(LocalDate.parse(availableUntil));
            product.setStockQuantity(stockQuantity);
            product.setCatalog(catalog.get());

            productRepository.save(product);
            System.out.println("✅ Product seeded!");


        });
    }

    private void seedOrderStatuses(JsonNode orderStatuses) {
        if (orderStatuses == null) {
            System.out.println("⚠️ JSON has no section 'orderStatuses'.");
            return;
        }

        orderStatuses.forEach(statusNode -> {
            String statusName = statusNode.get("name").asText();
            if (orderStatusRepository.findByName(statusName).isEmpty()) {
                OrderStatus status = new OrderStatus();
                status.setName(statusName);
                orderStatusRepository.save(status);
                System.out.println("✅ Order status seeded: " + statusName);
            }
        });
    }

    private void seedOrders(JsonNode orders) {
        if (orders == null) {
            System.out.println("⚠️ JSON has no section 'orders'.");
            return;
        }

        orders.forEach(orderNode -> {
            String username = orderNode.get("user").asText();
            String statusName = orderNode.get("status").asText();
            double totalPrice = orderNode.get("totalPrice").asDouble();
            String createdAt = orderNode.get("createdAt").asText();

            // Find the user
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                System.out.println("⚠️ User not found for order: " + username);
                return;
            }

            // Find the order status
            Optional<OrderStatus> status = orderStatusRepository.findByName(statusName);
            if (status.isEmpty()) {
                System.out.println("⚠️ Order status not found: " + statusName);
                return;
            }

            // Create and save the order
            Order order = new Order();
            order.setUser(user.get());
            order.setStatus(status.get());
            order.setTotalPrice(totalPrice);
            order.setCreatedAt(LocalDateTime.parse(createdAt));

            orderRepository.save(order);
            System.out.println("✅ Order seeded for user: " + username);

            // Seed order items for this order
            seedOrderItems(orderNode.get("orderItems"), order);
        });
    }

    private void seedOrderItems(JsonNode orderItems, Order order) {
        if (orderItems == null) {
            System.out.println("⚠️ No order items found for order ID: " + order.getOrderId());
            return;
        }

        orderItems.forEach(itemNode -> {
            String productName = itemNode.get("product").asText();
            int quantity = itemNode.get("quantity").asInt();

            // Find the product
            Optional<Product> product = productRepository.findByName(productName);
            if (product.isEmpty()) {
                System.out.println("⚠️ Product not found for order item: " + productName);
                return;
            }

            // Create and save the order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product.get());
            orderItem.setQuantity(quantity);

            orderItemRepository.save(orderItem);
            System.out.println("✅ Order item seeded for product: " + productName);
        });
    }
}
