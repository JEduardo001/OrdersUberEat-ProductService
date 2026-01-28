package com.SoftwareOrdersUberEats.productService.dto.product;

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
public class DtoProductsOrder {

    private UUID id;
    private UUID idProduct;
    private Integer quantityProducts;
    private Instant createAt;
    private Instant deleteAt;
}
