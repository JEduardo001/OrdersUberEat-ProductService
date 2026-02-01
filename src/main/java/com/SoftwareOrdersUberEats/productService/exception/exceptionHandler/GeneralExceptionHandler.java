package com.SoftwareOrdersUberEats.productService.exception.exceptionHandler;

import com.SoftwareOrdersUberEats.productService.dto.apiResponse.DtoResponseApiWithoutData;

import com.SoftwareOrdersUberEats.productService.service.MappedDiagnosticService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.SoftwareOrdersUberEats.productService.constant.TracerConstants.CORRELATION_KEY;

@ControllerAdvice
@Order(2)
@Slf4j
@AllArgsConstructor
public class GeneralExceptionHandler {

    private final MappedDiagnosticService mappedDiagnosticService;

    private ResponseEntity<DtoResponseApiWithoutData> buildResponse(HttpStatus status, String message, Exception ex) {
        log.warn("Business exception: {} - Message: {}", ex.getClass().getSimpleName(), message);

        return ResponseEntity.status(status)
                .body(DtoResponseApiWithoutData.builder()
                        .status(status.value())
                        .message(message)
                        .correlationId(mappedDiagnosticService.getIdCorrelation())
                        .build());
    }

    // Validaciones de parametros
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DtoResponseApiWithoutData> handleValidationException(MethodArgumentNotValidException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed for request parameters", ex);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<DtoResponseApiWithoutData> handleMissingRequestParam(MissingServletRequestParameterException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, "Missing required request parameter: " + ex.getParameterName(), ex);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<DtoResponseApiWithoutData> handleMissingPathVariable(MissingPathVariableException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, "Missing required path variable: " + ex.getVariableName(), ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DtoResponseApiWithoutData> handleHttpMessageNotReadable(HttpMessageNotReadableException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request",ex);
    }

    // Catch-all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DtoResponseApiWithoutData> handleGeneralException(Exception ex){
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred",ex);
    }
}
