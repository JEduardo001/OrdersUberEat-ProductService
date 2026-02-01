package com.SoftwareOrdersUberEats.productService.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.SoftwareOrdersUberEats.productService.constant.TracerConstants.CORRELATION_HEADER;
import static com.SoftwareOrdersUberEats.productService.constant.TracerConstants.CORRELATION_KEY;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;

@Service
public class Producer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Producer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String request, String nameTopic, String correlationId) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(nameTopic, request);

        if (correlationId != null) {
            record.headers().add(CORRELATION_HEADER, correlationId.getBytes());
        }

        kafkaTemplate.send(record);
    }

    public void publisFailedSendEventDlq(String request) {
        kafkaTemplate.send("failed.send.event.dlq", request);
    }
}