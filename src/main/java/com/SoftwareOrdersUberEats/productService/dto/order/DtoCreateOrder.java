package com.SoftwareOrdersUberEats.productService.dto.order;


import com.SoftwareOrdersUberEats.productService.dto.product.DtoProductsOrder;
import com.SoftwareOrdersUberEats.productService.enums.statusCreateResource.ResultCreateOrdenEnum;
import com.SoftwareOrdersUberEats.productService.enums.statusCreateResource.ResultEventEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DtoCreateOrder {
    @NotNull
    private UUID idOrder;
    @NotNull
    private UUID idUser;
    @NotNull
    private ResultEventEnum resultEvent;
    @NotNull
    private List<DtoProductsOrder> products;
}
