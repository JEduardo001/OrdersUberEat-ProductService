package com.SoftwareOrdersUberEats.productService.controller;

import com.SoftwareOrdersUberEats.productService.constant.ApiBase;
import com.SoftwareOrdersUberEats.productService.dto.apiResponse.DtoResponseApi;
import com.SoftwareOrdersUberEats.productService.dto.order.DtoCreateOrder;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoCreateProduct;
import com.SoftwareOrdersUberEats.productService.dto.product.DtoUpdateProduct;
import com.SoftwareOrdersUberEats.productService.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiBase.apiBase + "product")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;


    @GetMapping
    public ResponseEntity<DtoResponseApi> getAllProduct(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.status(HttpStatus.OK).body(DtoResponseApi.builder()
                .status(HttpStatus.OK.value())
                .message("Products obtained")
                .data(productService.getAll(page,size))
                .build()
        );
    }

    @GetMapping("/{idProduct}")
    public ResponseEntity<DtoResponseApi> getProduct(@PathVariable UUID idProduct){
        return ResponseEntity.status(HttpStatus.OK).body(DtoResponseApi.builder()
                .status(HttpStatus.OK.value())
                .message("Product obtained")
                .data(productService.get(idProduct))
                .build()
        );
    }

    @PutMapping()
    public ResponseEntity<DtoResponseApi> getProduct(@Valid @RequestBody DtoUpdateProduct request){
        return ResponseEntity.status(HttpStatus.OK).body(DtoResponseApi.builder()
                .status(HttpStatus.OK.value())
                .message("Product updated")
                .data(productService.update(request))
                .build()
        );
    }

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
