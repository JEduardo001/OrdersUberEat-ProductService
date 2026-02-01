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
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static com.SoftwareOrdersUberEats.productService.constant.TracerConstants.CORRELATION_HEADER;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;

@Service
@AllArgsConstructor
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


    @KafkaListener(topics = "order.created.pending", groupId = "orders")
    @Transactional
    @Override
    public void handleVerifyProductStock(String rawEvent, @Header(CORRELATION_HEADER) String correlationId) {
        System.out.println("xxx " + correlationId);

        //mappedDiagnosticService.setIdCorrelation(correlationId);
       String json = parseRawEvent(rawEvent);

       DtoEvent<DtoCreateOrder> dto = new ObjectMapper().readValue(
               json,
               new TypeReference<DtoEvent<DtoCreateOrder>>() {}
       );

       if(isEventProcessed(dto.getIdEvent())){
           return;
       }

       ResultEventEnum result = productService.verifyProductStock(dto.getData());
       String  topic = "inventory.stock.reserved";

       if(result != ResultEventEnum.UPDATED){
           topic = "inventory.stock.reserved.failed";
       }

       dto.getData().setResultEvent(result);
       DtoEvent<DtoCreateOrder> event = DtoEvent.<DtoCreateOrder>builder()
               .data(dto.getData())
               .idEvent(dto.getIdEvent())
               .correlationId(mappedDiagnosticService.getIdCorrelation())
               .typeEvent(TypeEventEnum.UPDATE)
               .build();

       outboxEventService.saveEvent(event, topic);
       saveEventProcessed(dto.getIdEvent());

    }

    @KafkaListener(topics = "changed.status.order.failed", groupId = "orders")
    @Transactional
    @Override
    public void handleRevertStockProducts(String rawEvent,@Header(CORRELATION_HEADER) String correlationId) {
        //mappedDiagnosticService.setIdCorrelation(correlationId);

        String json = parseRawEvent(rawEvent);

        DtoEvent<DtoCreateOrder> dto = new ObjectMapper().readValue(
                json,
                new TypeReference<DtoEvent<DtoCreateOrder>>() {}
        );

        if(isEventProcessed(dto.getIdEvent())){
            return;
        }

        productService.revertStock(dto.getData());
        saveEventProcessed(dto.getIdEvent());
    }
}
