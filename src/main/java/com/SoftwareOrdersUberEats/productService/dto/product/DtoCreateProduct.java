package com.SoftwareOrdersUberEats.productService.dto.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DtoCreateProduct {

    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Integer stock;
}
