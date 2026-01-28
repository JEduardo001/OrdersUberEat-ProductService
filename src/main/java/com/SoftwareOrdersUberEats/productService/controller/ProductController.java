package com.SoftwareOrdersUberEats.productService.controller;

import com.SoftwareOrdersUberEats.productService.constant.ApiBase;
import com.SoftwareOrdersUberEats.productService.dto.apiResponse.DtoResponseApi;
import com.SoftwareOrdersUberEats.productService.dto.order.DtoCreateOrder;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoCreateProduct;
import com.SoftwareOrdersUberEats.productService.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiBase.apiBase + "product")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<DtoResponseApi> createProduct(@Valid @RequestBody DtoCreateProduct request){
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoResponseApi.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("requested order")
                        .data(productService.create(request))
                .build()
        );
    }

}
