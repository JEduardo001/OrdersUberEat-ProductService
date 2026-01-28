package com.SoftwareOrdersUberEats.productService.dto.apiResponse;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DtoResponseApi<T> {
    private Integer status;
    private String message;
    private T data;
}