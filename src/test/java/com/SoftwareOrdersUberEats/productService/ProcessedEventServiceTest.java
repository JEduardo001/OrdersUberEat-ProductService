package com.SoftwareOrdersUberEats.productService;

import com.SoftwareOrdersUberEats.productService.entities.ProcessedEventEntity;
import com.SoftwareOrdersUberEats.productService.repository.ProcessedEventRepository;
import com.SoftwareOrdersUberEats.productService.service.ProcessedEventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessedEventServiceTest {

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private ProcessedEventService processedEventService;

    @Test
    @DisplayName("Should save event successfully when it's new")
    void save_Success() {
        UUID eventId = UUID.randomUUID();

        processedEventService.save(eventId);

        verify(processedEventRepository).save(any(ProcessedEventEntity.class));
    }

    @Test
    @DisplayName("Should catch exception and not crash when event already exists")
    void save_ShouldNotThrowException_WhenEventAlreadyExists() {
        UUID eventId = UUID.randomUUID();
        // Simulamos que el repositorio lanza una excepción (ej. violación de llave primaria)
        doThrow(new RuntimeException("Duplicate entry")).when(processedEventRepository).save(any());

        // Act & Assert
        assertDoesNotThrow(() -> processedEventService.save(eventId));
        verify(processedEventRepository).save(any());
    }

    @Test
    @DisplayName("Should return true when event exists in database")
    void isEventProcessed_ShouldReturnTrue_WhenExists() {
        UUID eventId = UUID.randomUUID();
        when(processedEventRepository.existsById(eventId)).thenReturn(true);

        boolean result = processedEventService.isEventProcessed(eventId);

        assertTrue(result);
        verify(processedEventRepository).existsById(eventId);
    }

    @Test
    @DisplayName("Should return false when event does not exist")
    void isEventProcessed_ShouldReturnFalse_WhenNotExists() {
        UUID eventId = UUID.randomUUID();
        when(processedEventRepository.existsById(eventId)).thenReturn(false);

        boolean result = processedEventService.isEventProcessed(eventId);

        assertFalse(result);
    }
}