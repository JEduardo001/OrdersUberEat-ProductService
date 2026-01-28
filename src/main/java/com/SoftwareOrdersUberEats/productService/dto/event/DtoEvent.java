package com.SoftwareOrdersUberEats.productService.dto.event;

import com.SoftwareOrdersUberEats.productService.enums.typeEvents.TypeEventEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DtoEvent<T> {
    private TypeEventEnum typeEvent;
    private UUID idEvent;
    private T data;
}

