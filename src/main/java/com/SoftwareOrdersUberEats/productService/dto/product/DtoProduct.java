package com.SoftwareOrdersUberEats.productService.dto.product;

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
public class DtoProduct {

    private UUID id;
    private String name;
    private String description;
    private Integer stock;
    private Instant createAt;
    private Instant deletedAt;
}
