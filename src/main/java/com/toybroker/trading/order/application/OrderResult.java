package com.toybroker.trading.order.application;

import com.toybroker.trading.order.domain.OrderStatus;
import java.time.Instant;
import java.util.UUID;

public record OrderResult(
        UUID orderId,
        UUID accountId,
        String symbol,
        OrderStatus status,
        Instant createdAt
) {
}
