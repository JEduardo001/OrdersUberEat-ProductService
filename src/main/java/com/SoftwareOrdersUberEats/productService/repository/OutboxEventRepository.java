package com.SoftwareOrdersUberEats.productService.repository;

import com.SoftwareOrdersUberEats.productService.entities.OutboxEventEntity;
import com.SoftwareOrdersUberEats.productService.entities.ProductEntity;
import com.SoftwareOrdersUberEats.productService.enums.StatusEventEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findAllByStatusEvent(StatusEventEnum status);
}
