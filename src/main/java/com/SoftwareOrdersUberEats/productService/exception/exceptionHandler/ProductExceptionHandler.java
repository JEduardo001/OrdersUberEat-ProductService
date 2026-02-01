package com.SoftwareOrdersUberEats.productService.exception.exceptionHandler;

import com.SoftwareOrdersUberEats.productService.dto.apiResponse.DtoResponseApiWithoutData;
import com.SoftwareOrdersUberEats.productService.exception.product.NameProductAlreadyExistException;
import com.SoftwareOrdersUberEats.productService.exception.product.ProductNotFoundException;
import com.SoftwareOrdersUberEats.productService.exception.product.ProductOutOfStockException;
import com.SoftwareOrdersUberEats.productService.service.MappedDiagnosticService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(1)
@Slf4j
@AllArgsConstructor
public class ProductExceptionHandler {

    private final MappedDiagnosticService mappedDiagnosticService;

    private ResponseEntity<DtoResponseApiWithoutData> buildResponse(HttpStatus status, String message, Exception ex) {
        log.warn("Business exception: {} - Message: {}", ex.getClass().getSimpleName(), message);

        return ResponseEntity.status(status)
                .body(DtoResponseApiWithoutData.builder()
                        .status(status.value())
                        .correlationId(mappedDiagnosticService.getIdCorrelation())
                        .message(message)
                        .build());
    }

    // Validaciones de parametros
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<DtoResponseApiWithoutData> orderNotFoundException(ProductNotFoundException ex){
        return buildResponse(HttpStatus.NOT_FOUND, "product not found", ex);
    }

    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<DtoResponseApiWithoutData> productOutOfStockException(ProductOutOfStockException ex){
        return buildResponse(HttpStatus.CONFLICT, "There is not enough stock of the product",ex);
    }

    @ExceptionHandler(NameProductAlreadyExistException.class)
    public ResponseEntity<DtoResponseApiWithoutData> NameProductAlreadyExistException(NameProductAlreadyExistException ex){
        return buildResponse(HttpStatus.CONFLICT, "Name product already in use", ex);
    }
}
