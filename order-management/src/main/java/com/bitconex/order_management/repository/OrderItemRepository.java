package com.bitconex.order_management.repository;

import com.bitconex.order_management.entity.OrderItem;
import com.bitconex.order_management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    void deleteByProduct(Product product);

    List<OrderItem> findAllByProduct(Product product);
}
