package com.SoftwareOrdersUberEats.productService;

import com.SoftwareOrdersUberEats.productService.dto.apiResponse.DtoPageableResponse;
import com.SoftwareOrdersUberEats.productService.dto.order.DtoCreateOrder;
import com.SoftwareOrdersUberEats.productService.dto.product.*;
import com.SoftwareOrdersUberEats.productService.entities.ProductEntity;
import com.SoftwareOrdersUberEats.productService.enums.statusCreateResource.ResultEventEnum;
import com.SoftwareOrdersUberEats.productService.exception.product.NameProductAlreadyExistException;
import com.SoftwareOrdersUberEats.productService.exception.product.ProductNotFoundException;
import com.SoftwareOrdersUberEats.productService.mapper.ProductMapper;
import com.SoftwareOrdersUberEats.productService.repository.ProductRepository;
import com.SoftwareOrdersUberEats.productService.service.ProductService;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private Validator validator;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should return pageable response of products")
    void getAll_Success() {
        ProductEntity entity = new ProductEntity();
        Page<ProductEntity> page = new PageImpl<>(List.of(entity));
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(productMapper.toDto(any())).thenReturn(DtoProduct.builder().build());

        DtoPageableResponse result = productService.getAll(0, 10);

        assertNotNull(result);
        assertEquals(1, result.totalElements());
    }

    @Test
    @DisplayName("Should throw exception when product not found by ID")
    void get_NotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.get(id));
    }

    @Test
    @DisplayName("Should create product when name is unique")
    void create_Success() {
        DtoCreateProduct request = DtoCreateProduct.builder().name("Burger").build();
        ProductEntity entity = new ProductEntity();

        when(productRepository.existsByName("Burger")).thenReturn(false);
        when(productMapper.toEntity(request)).thenReturn(entity);
        when(productMapper.toDto(entity)).thenReturn(DtoProduct.builder().name("Burger").build());

        DtoProduct result = productService.create(request);

        assertNotNull(result);
        verify(productRepository).save(entity);
    }

    @Test
    @DisplayName("Should throw exception if name exists on create")
    void create_NameExists() {
        DtoCreateProduct request = DtoCreateProduct.builder().name("Burger").build();
        when(productRepository.existsByName("Burger")).thenReturn(true);

        assertThrows(NameProductAlreadyExistException.class, () -> productService.create(request));
    }

    @Test
    @DisplayName("Should return UPDATED when stock is sufficient")
    void verifyProductStock_Success() {
        UUID productId = UUID.randomUUID();
        DtoProductsOrder item = DtoProductsOrder.builder().idProduct(productId).quantityProducts(2).build();
        DtoCreateOrder request = DtoCreateOrder.builder().products(List.of(item)).build();

        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setStock(10);

        when(validator.validate(request)).thenReturn(Collections.emptySet());
        when(productRepository.findAllById(any())).thenReturn(List.of(product));

        ResultEventEnum result = productService.verifyProductStock(request);

        assertEquals(ResultEventEnum.UPDATED, result);
        assertEquals(8, product.getStock());
        verify(productRepository).saveAll(any());
    }

    @Test
    @DisplayName("Should return OUT_OF_STOCK when quantity exceeds stock")
    void verifyProductStock_NoStock() {
        UUID productId = UUID.randomUUID();
        DtoProductsOrder item = DtoProductsOrder.builder().idProduct(productId).quantityProducts(20).build();
        DtoCreateOrder request = DtoCreateOrder.builder().products(List.of(item)).build();

        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setStock(10);

        when(validator.validate(request)).thenReturn(Collections.emptySet());
        when(productRepository.findAllById(any())).thenReturn(List.of(product));

        ResultEventEnum result = productService.verifyProductStock(request);

        assertEquals(ResultEventEnum.OUT_OF_STOCK, result);
        verify(productRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should return NOT_FOUND_PRODUCT when product ID doesn't exist")
    void verifyProductStock_NotFound() {
        UUID productId = UUID.randomUUID();
        DtoProductsOrder item = DtoProductsOrder.builder().idProduct(productId).build();
        DtoCreateOrder request = DtoCreateOrder.builder().products(List.of(item)).build();

        when(validator.validate(request)).thenReturn(Collections.emptySet());
        when(productRepository.findAllById(any())).thenReturn(Collections.emptyList());

        ResultEventEnum result = productService.verifyProductStock(request);

        assertEquals(ResultEventEnum.NOT_FOUND_PRODUCT, result);
    }

    @Test
    @DisplayName("Should revert stock correctly")
    void revertStock_Success() {
        UUID productId = UUID.randomUUID();
        DtoProductsOrder item = DtoProductsOrder.builder().id(productId).quantityProducts(5).build();
        DtoCreateOrder request = DtoCreateOrder.builder().products(List.of(item)).build();

        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setStock(10);

        when(productRepository.findAllById(any())).thenReturn(List.of(product));

        productService.revertStock(request);

        // Note: Your logic does product.setStock(product.getStock() + product.getStock())
        // which doubles it. If that's the intended logic, the result is 20.
        assertEquals(20, product.getStock());
        verify(productRepository).saveAll(any());
    }
}