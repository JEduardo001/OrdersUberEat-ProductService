package com.SoftwareOrdersUberEats.productService.dto.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class DtoUpdateProduct {
    @NotNull
    private UUID id;
    @NotEmpty
    private String name;
    private String description;
    @NotNull
    @Positive
    private Integer stock;
}
