package com.SoftwareOrdersUberEats.productService.interfaces;

import org.springframework.messaging.handler.annotation.Header;

import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;

public interface IConsumer {
    void handleVerifyProductStock(String rawEvent,String correlationId);
    void handleRevertStockProducts(String rawEvent,String correlationId);
}
