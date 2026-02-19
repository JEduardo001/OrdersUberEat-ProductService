package com.SoftwareOrdersUberEats.productService.service;

import com.SoftwareOrdersUberEats.productService.dto.apiResponse.DtoPageableResponse;
import com.SoftwareOrdersUberEats.productService.dto.order.DtoCreateOrder;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoCreateProduct;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoProduct;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoProductsOrder;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoUpdateProduct;
import com.SoftwareOrdersUberEats.productService.entities.ProductEntity;
import com.SoftwareOrdersUberEats.productService.enums.statusCreateResource.ResultEventEnum;

import com.SoftwareOrdersUberEats.productService.exception.product.NameProductAlreadyExistException;
import com.SoftwareOrdersUberEats.productService.exception.product.ProductNotFoundException;
import com.SoftwareOrdersUberEats.productService.interfaces.IProductService;
import com.SoftwareOrdersUberEats.productService.mapper.ProductMapper;
import com.SoftwareOrdersUberEats.productService.repository.ProductRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.SoftwareOrdersUberEats.productService.constant.TracerConstants.*;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService implements IProductService {

    private ProductRepository productRepository;
    private Validator validator;
    private ProductMapper productMapper;

    @Override
    public DtoPageableResponse getAll(int page,int size){
        Page<ProductEntity> products = productRepository.findAll(PageRequest.of(page,size));
        List<DtoProduct> listProducts = products.get().map(productMapper::toDto).collect(Collectors.toList());
        return new DtoPageableResponse(
                products.getTotalElements(),
                products.getTotalPages(),
                listProducts
        );
    }

    @Override
    public DtoProduct get(UUID id){
        return productMapper.toDto(productRepository.findById(id).orElseThrow(ProductNotFoundException::new));
    }

    @Override
    public DtoProduct update(DtoUpdateProduct request){
        ProductEntity actualProduct = productRepository.findById(request.getId()).orElseThrow(ProductNotFoundException::new);

        if(productRepository.existsByNameAndIdNot(request.getName(),request.getId())){
            throw new NameProductAlreadyExistException();
        }

        productMapper.updateProduct(request,actualProduct);
        ProductEntity product = productRepository.save(actualProduct);
        log.info(MESSAGE_UPDATE_PRODUCT);
        return productMapper.toDto(product);
    }


    @Override
    @Transactional
    public DtoProduct create(DtoCreateProduct request){

        if(productRepository.existsByName(request.getName())){
            throw new NameProductAlreadyExistException();
        }

        ProductEntity product = productMapper.toEntity(request);
        product.setCreateAt(Instant.now());

        ProductEntity savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public ResultEventEnum verifyProductStock(DtoCreateOrder request) {


          Set<ConstraintViolation<DtoCreateOrder>> violations = validator.validate(request);
          if (!violations.isEmpty()) {
              log.info(MESSAGE_DATA_VALIDATION_VERIFY_STOCK_ERROR);
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
        log.info(MESSAGE_REVERSED_STOCK);
    }
}
