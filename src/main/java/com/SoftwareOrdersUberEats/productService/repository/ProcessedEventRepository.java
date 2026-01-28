package com.SoftwareOrdersUberEats.productService.repository;

import com.SoftwareOrdersUberEats.productService.entities.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, UUID> {
}