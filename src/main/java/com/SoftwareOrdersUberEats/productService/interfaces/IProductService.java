package com.SoftwareOrdersUberEats.productService.interfaces;

import com.SoftwareOrdersUberEats.productService.dto.apiResponse.DtoPageableResponse;
import com.SoftwareOrdersUberEats.productService.dto.order.DtoCreateOrder;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoCreateProduct;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoProduct;

import com.SoftwareOrdersUberEats.productService.dto.product.DtoUpdateProduct;
import com.SoftwareOrdersUberEats.productService.enums.statusCreateResource.ResultEventEnum;

import java.util.UUID;

public interface IProductService {
    ResultEventEnum verifyProductStock(DtoCreateOrder request);
    DtoProduct create(DtoCreateProduct request);
    void revertStock(DtoCreateOrder request);
    DtoProduct update(DtoUpdateProduct request);
    DtoProduct get(UUID id);
    DtoPageableResponse getAll(int page, int size);
}
