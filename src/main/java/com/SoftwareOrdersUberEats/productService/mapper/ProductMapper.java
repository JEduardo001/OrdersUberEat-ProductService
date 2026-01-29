package com.SoftwareOrdersUberEats.productService.mapper;

import com.SoftwareOrdersUberEats.productService.dto.product.DtoCreateProduct;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoProduct;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoProductsOrder;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoUpdateProduct;
import com.SoftwareOrdersUberEats.productService.entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductEntity toEntity(DtoCreateProduct request);
    DtoProduct toDto(ProductEntity request);
    @Mapping(target = "id", ignore = true)
    void updateProduct(DtoUpdateProduct request,@MappingTarget ProductEntity actualProduct);
}
