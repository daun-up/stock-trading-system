package com.toybroker.trading.outbox.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 100)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "published", nullable = false)
    private boolean published;

    protected OutboxEvent() {
    }

    private OutboxEvent(
            UUID id,
            String aggregateType,
            String aggregateId,
            String eventType,
            String payload,
            Instant createdAt,
            boolean published
    ) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = createdAt;
        this.published = published;
    }

    public static OutboxEvent pending(
            String aggregateType,
            String aggregateId,
            String eventType,
            String payload
    ) {
        return new OutboxEvent(
                UUID.randomUUID(),
                aggregateType,
                aggregateId,
                eventType,
                payload,
                Instant.now(),
                false
        );
    }
}
