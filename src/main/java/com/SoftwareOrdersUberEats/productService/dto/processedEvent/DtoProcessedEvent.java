package com.SoftwareOrdersUberEats.productService.dto.processedEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DtoProcessedEvent {
    private UUID id;
    private Instant processedAt;
}

