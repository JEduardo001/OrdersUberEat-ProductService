package com.SoftwareOrdersUberEats.productService.repository;

import com.SoftwareOrdersUberEats.productService.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    boolean existsByNameAndIdNot(String name,UUID id);
    boolean existsByName(String name);
}
