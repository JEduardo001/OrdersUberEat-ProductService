package com.SoftwareOrdersUberEats.productService.entities;

import com.SoftwareOrdersUberEats.productService.enums.StatusEventEnum;
import com.SoftwareOrdersUberEats.productService.enums.typeEvents.TypeEventEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "outbox_table")
public class OutboxEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nameTopic;
    @Column(columnDefinition = "TEXT")
    private String payload;
    @Enumerated(EnumType.STRING)
    private TypeEventEnum typeEvent;
    @Enumerated(EnumType.STRING)
    private StatusEventEnum statusEvent;
    private Integer retryCount;
    private Instant created_at;
}
