package com.SoftwareOrdersUberEats.productService.mapper;

import com.SoftwareOrdersUberEats.productService.dto.product.DtoCreateProduct;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoProduct;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoProductsOrder;
import com.SoftwareOrdersUberEats.productService.entities.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductEntity toEntity(DtoCreateProduct request);
    DtoProduct toDto(ProductEntity request);
}
