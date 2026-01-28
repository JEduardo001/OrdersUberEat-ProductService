package com.SoftwareOrdersUberEats.productService.exception.exceptionHandler;

import com.SoftwareOrdersUberEats.productService.dto.apiResponse.DtoResponseApiWithoutData;
import com.SoftwareOrdersUberEats.productService.exception.product.ProductNotFoundException;
import com.SoftwareOrdersUberEats.productService.exception.product.ProductOutOfStockException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(1)
public class ProductExceptionHandler {

    private ResponseEntity<DtoResponseApiWithoutData> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(DtoResponseApiWithoutData.builder()
                        .status(status.value())
                        .message(message)
                        .build());
    }

    // Validaciones de parametros
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<DtoResponseApiWithoutData> orderNotFoundException(ProductNotFoundException ex){
        return buildResponse(HttpStatus.NOT_FOUND, "product not found");
    }

    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<DtoResponseApiWithoutData> productOutOfStockException(ProductOutOfStockException ex){
        return buildResponse(HttpStatus.CONFLICT, "There is not enough stock of the product");
    }
}
