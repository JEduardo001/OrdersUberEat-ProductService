package com.SoftwareOrdersUberEats.productService.interfaces;

import com.SoftwareOrdersUberEats.productService.dto.order.DtoCreateOrder;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoCreateProduct;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoProduct;

import com.SoftwareOrdersUberEats.productService.enums.statusCreateResource.ResultEventEnum;

public interface IProductService {
    ResultEventEnum verifyProductStock(DtoCreateOrder request);
    DtoProduct create(DtoCreateProduct request);
    void revertStock(DtoCreateOrder request);
}
