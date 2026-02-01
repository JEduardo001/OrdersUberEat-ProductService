package com.SoftwareOrdersUberEats.productService.kafka.config;


import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.stereotype.Component;

import static com.SoftwareOrdersUberEats.productService.constant.TracerConstants.CORRELATION_HEADER;
import static com.SoftwareOrdersUberEats.productService.constant.TracerConstants.CORRELATION_KEY;

@Component
public class CorrelationIdInterceptor implements RecordInterceptor<String, String> {

    @Override
    public ConsumerRecord<String, String> intercept(ConsumerRecord<String, String> record, Consumer<String, String> consumer) {
        var header = record.headers().lastHeader(CORRELATION_HEADER);

        String correlationId;
        if (header != null && header.value() != null) {
            correlationId = new String(header.value());
        } else {
            correlationId = java.util.UUID.randomUUID().toString();
        }

        MDC.put(CORRELATION_KEY, correlationId);

        return record;
    }

    @Override
    public void afterRecord(ConsumerRecord<String, String> record, Consumer<String, String> consumer) {
        MDC.remove(CORRELATION_KEY);
    }
}
