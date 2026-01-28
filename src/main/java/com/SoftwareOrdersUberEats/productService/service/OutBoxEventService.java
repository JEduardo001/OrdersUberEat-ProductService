package com.SoftwareOrdersUberEats.productService.service;

import com.SoftwareOrdersUberEats.productService.dto.event.DtoEvent;
import com.SoftwareOrdersUberEats.productService.entities.OutboxEventEntity;
import com.SoftwareOrdersUberEats.productService.enums.StatusEventEnum;
import com.SoftwareOrdersUberEats.productService.kafka.producer.Producer;
import com.SoftwareOrdersUberEats.productService.repository.OutboxEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class OutBoxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final Producer producer;
    private final ObjectMapper objectMapper;

    public void saveEvent(DtoEvent request, String nameTopic){
        outboxEventRepository.save(OutboxEventEntity.builder()
                .payload(objectMapper.writeValueAsString(request))
                .nameTopic(nameTopic)
                .typeEvent(request.getTypeEvent())
                .statusEvent(StatusEventEnum.PENDING)
                .retryCount(0)
                .created_at(Instant.now())
                .build());
    }


    @Scheduled(fixedDelay = 500)
    public void publishPendingEvents() {
        List<OutboxEventEntity> events = outboxEventRepository.findAllByStatusEvent(StatusEventEnum.PENDING);

        for (OutboxEventEntity e : events) {
            try {
                producer.send(e.getPayload(),e.getNameTopic());
                e.setStatusEvent(StatusEventEnum.SENT);
                outboxEventRepository.save(e);
            } catch (Exception ex) {

                e.setRetryCount(e.getRetryCount() + 1);
                if (e.getRetryCount() > 20) {
                    e.setStatusEvent(StatusEventEnum.FAILED);
                    producer.send(e.getPayload(),"failed.send.event.dlq");
                }

                outboxEventRepository.save(e);
            }
        }
    }
}