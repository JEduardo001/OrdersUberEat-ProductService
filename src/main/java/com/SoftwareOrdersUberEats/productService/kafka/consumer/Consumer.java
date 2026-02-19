package com.SoftwareOrdersUberEats.productService.kafka.consumer;

import com.SoftwareOrdersUberEats.productService.dto.event.DtoEvent;
import com.SoftwareOrdersUberEats.productService.dto.order.DtoCreateOrder;
import com.SoftwareOrdersUberEats.productService.enums.statusCreateResource.ResultEventEnum;
import com.SoftwareOrdersUberEats.productService.enums.typeEvents.TypeEventEnum;
import com.SoftwareOrdersUberEats.productService.interfaces.IConsumer;
import com.SoftwareOrdersUberEats.productService.service.MappedDiagnosticService;
import com.SoftwareOrdersUberEats.productService.service.OutBoxEventService;
import com.SoftwareOrdersUberEats.productService.service.ProcessedEventService;
import com.SoftwareOrdersUberEats.productService.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import static com.SoftwareOrdersUberEats.productService.constant.TracerConstants.*;

import java.time.Instant;
import java.util.UUID;

import static com.SoftwareOrdersUberEats.productService.constant.TracerConstants.CORRELATION_HEADER;

@Service
@AllArgsConstructor
@Slf4j
public class Consumer implements IConsumer {

    private final ProductService productService;
    private final OutBoxEventService outboxEventService;
    private final ProcessedEventService processedEventService;
    private final MappedDiagnosticService mappedDiagnosticService;

    private String parseRawEvent(String rawEvent){
        String json = rawEvent;
        if (rawEvent.startsWith("\"") && rawEvent.endsWith("\"")) {
            json = new ObjectMapper().readValue(rawEvent, String.class);
        }
        return json;
    }

    private boolean isEventProcessed(UUID id){
        return processedEventService.isEventProcessed(id);
    }

    private void saveEventProcessed(UUID id){
        processedEventService.save(id);
    }

    private void createEvent(DtoEvent<DtoCreateOrder> request, String topic, ResultEventEnum result ){
        request.getData().setResultEvent(result);
        DtoEvent<DtoCreateOrder> event = DtoEvent.<DtoCreateOrder>builder()
                .data(request.getData())
                .idEvent(request.getIdEvent())
                .correlationId(mappedDiagnosticService.getIdCorrelation())
                .createAt(request.getCreateAt())
                .typeEvent(TypeEventEnum.UPDATE)
                .build();

        outboxEventService.saveEvent(event, topic);
    }

    @KafkaListener(topics = "dev.order-ms.order-created-pending.v1", groupId = "order-ms.order-created-pending.v1")
    @Transactional
    @Override
    public void handleVerifyProductStock(String rawEvent, @Header(CORRELATION_HEADER) String correlationId) {


            String json = parseRawEvent(rawEvent);

            DtoEvent<DtoCreateOrder> dto = new ObjectMapper().readValue(
                    json,
                    new TypeReference<>() {}
            );

            if(isEventProcessed(dto.getIdEvent())){
                return;
            }

            Instant dateCreateOrder = dto.getCreateAt();
            Instant limitDate = dateCreateOrder.plusSeconds(86400); // 24 hours limit
            String topicFailed = "dev.product-ms.inventory-stock-reserved-failed.v1";
            if (Instant.now().isAfter(limitDate)) {
                log.info(MESSAGE_ORDER_TIME_LIMIT_EXCEEDED, dto.getCorrelationId());
                createEvent(dto,topicFailed,ResultEventEnum.TIME_LIMIT_EXCEEDED_TO_PROCESS);
                saveEventProcessed(dto.getIdEvent());
                return;
            }

            ResultEventEnum result = productService.verifyProductStock(dto.getData());

            if(result != ResultEventEnum.UPDATED){
                createEvent(dto,topicFailed, result);
            }else{
                createEvent(dto,"dev.product-ms.inventory-stock.reserved.v1", result);
            }

            saveEventProcessed(dto.getIdEvent());
    }

    @KafkaListener(topics = "dev.order-ms.changed-status-order-failed.v1", groupId = "order-ms.changed-status-order-failed.v1")
    @Transactional
    @Override
    public void handleRevertStockProducts(String rawEvent,@Header(CORRELATION_HEADER) String correlationId) {

        String json = parseRawEvent(rawEvent);

        DtoEvent<DtoCreateOrder> dto = new ObjectMapper().readValue(
                json,
                new TypeReference<>() {}
        );

        if(isEventProcessed(dto.getIdEvent())){
            return;
        }

        productService.revertStock(dto.getData());
        saveEventProcessed(dto.getIdEvent());
    }
}
