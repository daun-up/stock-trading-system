package com.toybroker.trading.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrderSide side;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 10)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal price;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Order() {
    }

    private Order(
            UUID id,
            UUID accountId,
            String symbol,
            OrderSide side,
            OrderType orderType,
            OrderStatus status,
            BigDecimal quantity,
            BigDecimal price,
            String idempotencyKey,
            Instant createdAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.symbol = symbol;
        this.side = side;
        this.orderType = orderType;
        this.status = status;
        this.quantity = quantity;
        this.price = price;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
    }

    public static Order pending(
            UUID accountId,
            String symbol,
            OrderSide side,
            OrderType orderType,
            BigDecimal quantity,
            BigDecimal price,
            String idempotencyKey
    ) {
        return new Order(
                UUID.randomUUID(),
                accountId,
                symbol,
                side,
                orderType,
                OrderStatus.PENDING,
                quantity,
                price,
                idempotencyKey,
                Instant.now()
        );
    }

    public void accept() {
        this.status = OrderStatus.ACCEPTED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderSide getSide() {
        return side;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
