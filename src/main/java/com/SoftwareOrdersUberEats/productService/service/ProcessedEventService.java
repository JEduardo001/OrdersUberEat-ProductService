package com.SoftwareOrdersUberEats.productService.service;

import com.SoftwareOrdersUberEats.productService.entities.ProcessedEventEntity;
import com.SoftwareOrdersUberEats.productService.repository.ProcessedEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProcessedEventService {

    private ProcessedEventRepository processedEventRepository;

    public void save(UUID id){
      try{
          processedEventRepository.save(ProcessedEventEntity.builder()
                  .id(id)
                  .processedAt(Instant.now())
                  .build());
      }catch(Exception e){
          //log event already exist
      }
    }

    public boolean isEventProcessed(UUID id){
        return processedEventRepository.existsById(id);
    }
}
