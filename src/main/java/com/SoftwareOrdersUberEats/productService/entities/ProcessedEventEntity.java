package com.SoftwareOrdersUberEats.productService.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "processed_event_table")
public class ProcessedEventEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    private Instant processedAt;
}
