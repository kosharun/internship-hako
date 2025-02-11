package com.bitconex.order_management.repository;

import com.bitconex.order_management.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogRepository extends JpaRepository<Catalog, Long> {
    Optional<Catalog> findByName(String name);
    Optional<Catalog> findFirstByOrderById(); // 🔹 Fetch the first (default) catalog
}
