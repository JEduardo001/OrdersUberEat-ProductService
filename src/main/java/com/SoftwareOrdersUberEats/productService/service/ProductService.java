package com.SoftwareOrdersUberEats.productService.service;

import com.SoftwareOrdersUberEats.productService.dto.order.DtoCreateOrder;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoCreateProduct;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoProduct;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoProductsOrder;
import com.SoftwareOrdersUberEats.productService.entities.ProductEntity;
import com.SoftwareOrdersUberEats.productService.enums.statusCreateResource.ResultEventEnum;

import com.SoftwareOrdersUberEats.productService.interfaces.IProductService;
import com.SoftwareOrdersUberEats.productService.mapper.ProductMapper;
import com.SoftwareOrdersUberEats.productService.repository.ProductRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService implements IProductService {

    private ProductRepository productRepository;
    private Validator validator;
    private ProductMapper productMapper;

    public List<ProductEntity> getAll(){
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public DtoProduct create(DtoCreateProduct request){

        ProductEntity product = productMapper.toEntity(request);
        product.setCreateAt(Instant.now());

        productRepository.save(product);

        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ResultEventEnum verifyProductStock(DtoCreateOrder request) {

        Set<ConstraintViolation<DtoCreateOrder>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            return ResultEventEnum.VALIDATION_ERROR;
        }

        List<UUID> ids = request.getProducts()
                .stream()
                .map(DtoProductsOrder::getIdProduct)
                .toList();

        List<ProductEntity> products = productRepository.findAllById(ids);

        Map<UUID, ProductEntity> productMap = products.stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        for (DtoProductsOrder dto : request.getProducts()) {
            ProductEntity product = productMap.get(dto.getIdProduct());

            if (product == null) {
               return ResultEventEnum.NOT_FOUND_PRODUCT;
            }

            if (product.getStock() < dto.getQuantityProducts()) {
                return ResultEventEnum.OUT_OF_STOCK;
            }

            product.setStock(product.getStock() - dto.getQuantityProducts());
        }

        productRepository.saveAll(products);
        return ResultEventEnum.UPDATED;
    }

    @Override
    @Transactional
    public void revertStock(DtoCreateOrder request){

        List<UUID> ids = request.getProducts()
                .stream()
                .map(DtoProductsOrder::getId)
                .toList();

        List<ProductEntity> products = productRepository.findAllById(ids);

        Map<UUID, ProductEntity> productMap = products.stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        for (DtoProductsOrder dto : request.getProducts()) {
            ProductEntity product = productMap.get(dto.getId());

            if (product != null) {
                product.setStock(product.getStock() + product.getStock());
            }
        }

        productRepository.saveAll(products);
    }


}
