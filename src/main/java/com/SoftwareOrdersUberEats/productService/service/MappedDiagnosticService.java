package com.SoftwareOrdersUberEats.productService.service;


import com.SoftwareOrdersUberEats.productService.interfaces.IMappedDiagnostic;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import static com.SoftwareOrdersUberEats.productService.constant.TracerConstants.CORRELATION_KEY;

@NoArgsConstructor
@Service
public class MappedDiagnosticService implements IMappedDiagnostic {

    public void setIdCorrelation(String correlationId){
        try {
            MDC.put(CORRELATION_KEY, correlationId);
        } finally {
            MDC.remove(CORRELATION_KEY);
        }
    }

    public String getIdCorrelation(){
        return MDC.get(CORRELATION_KEY);
    }
}