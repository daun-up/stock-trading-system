package com.toybroker.trading.order.application.command;

import com.toybroker.trading.order.domain.OrderSide;
import com.toybroker.trading.order.domain.OrderType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PlaceOrderCommand(
        @NotNull UUID accountId,
        @NotBlank String symbol,
        @NotNull OrderSide side,
        @NotNull OrderType orderType,
        @NotNull @DecimalMin("0.0001") BigDecimal quantity,
        BigDecimal price,
        @NotBlank String idempotencyKey
) {
}
