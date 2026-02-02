package com.SoftwareOrdersUberEats.productService;

import com.SoftwareOrdersUberEats.productService.dto.event.DtoEvent;
import com.SoftwareOrdersUberEats.productService.entities.OutboxEventEntity;
import com.SoftwareOrdersUberEats.productService.enums.StatusEventEnum;
import com.SoftwareOrdersUberEats.productService.enums.typeEvents.TypeEventEnum;
import com.SoftwareOrdersUberEats.productService.kafka.producer.Producer;
import com.SoftwareOrdersUberEats.productService.repository.OutboxEventRepository;
import com.SoftwareOrdersUberEats.productService.service.OutBoxEventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import java.util.UUID;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutBoxEventServiceTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private Producer producer;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutBoxEventService outBoxEventService;

    @Test
    @DisplayName("Should save event successfully with PENDING status")
    void saveEvent_Success() throws Exception {
        DtoEvent request = DtoEvent.builder()
                .correlationId("corr-123")
                .typeEvent(TypeEventEnum.UPDATE)
                .build();
        String topic = "products-topic";

        when(objectMapper.writeValueAsString(request)).thenReturn("{\"id\":1}");

        outBoxEventService.saveEvent(request, topic);

        verify(outboxEventRepository).save(argThat(entity ->
                entity.getStatusEvent() == StatusEventEnum.PENDING &&
                        entity.getNameTopic().equals(topic) &&
                        entity.getCorrelationId().equals("corr-123")
        ));
    }

    @Test
    @DisplayName("Should set status to SENT when producer sends successfully")
    void publishPendingEvents_Success() {
        OutboxEventEntity event = OutboxEventEntity.builder()
                .id(UUID.randomUUID())
                .payload("{}")
                .nameTopic("topic")
                .correlationId("id")
                .statusEvent(StatusEventEnum.PENDING)
                .build();

        when(outboxEventRepository.findAllByStatusEvent(StatusEventEnum.PENDING))
                .thenReturn(List.of(event));

        outBoxEventService.publishPendingEvents();

        assertEquals(StatusEventEnum.SENT, event.getStatusEvent());
        verify(producer).send(any(), any(), any());
        verify(outboxEventRepository).save(event);
    }

    @Test
    @DisplayName("Should increment retry count when producer fails")
    void publishPendingEvents_ShouldIncrementRetry_WhenError() {
        OutboxEventEntity event = OutboxEventEntity.builder()
                .retryCount(0)
                .statusEvent(StatusEventEnum.PENDING)
                .build();

        when(outboxEventRepository.findAllByStatusEvent(StatusEventEnum.PENDING))
                .thenReturn(List.of(event));
        doThrow(new RuntimeException("Kafka error")).when(producer).send(any(), any(), any());

        outBoxEventService.publishPendingEvents();

        assertEquals(1, event.getRetryCount());
        verify(outboxEventRepository).save(event);
    }

    @Test
    @DisplayName("Should move to FAILED and send to DLQ after 20 retries")
    void publishPendingEvents_ShouldMoveToFailed_WhenMaxRetriesReached() {
        OutboxEventEntity event = OutboxEventEntity.builder()
                .retryCount(21)
                .payload("{}")
                .statusEvent(StatusEventEnum.PENDING)
                .build();

        when(outboxEventRepository.findAllByStatusEvent(StatusEventEnum.PENDING))
                .thenReturn(List.of(event));
        doThrow(new RuntimeException("Final Kafka error")).when(producer).send(any(), any(), any());

        outBoxEventService.publishPendingEvents();

        assertEquals(StatusEventEnum.FAILED, event.getStatusEvent());
        verify(producer).publisFailedSendEventDlq(any());
        verify(outboxEventRepository).save(event);
    }
}