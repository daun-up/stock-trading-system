package com.toybroker.trading.order.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toybroker.trading.account.domain.AccountBalance;
import com.toybroker.trading.account.domain.AccountBalanceRepository;
import com.toybroker.trading.order.application.command.PlaceOrderCommand;
import com.toybroker.trading.order.domain.Order;
import com.toybroker.trading.order.domain.OrderRepository;
import com.toybroker.trading.order.domain.OrderSide;
import com.toybroker.trading.outbox.domain.OutboxEvent;
import com.toybroker.trading.outbox.domain.OutboxEventRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AccountBalanceRepository accountBalanceRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OrderService(
            OrderRepository orderRepository,
            AccountBalanceRepository accountBalanceRepository,
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper
    ) {
        this.orderRepository = orderRepository;
        this.accountBalanceRepository = accountBalanceRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderResult placeOrder(PlaceOrderCommand command) {
        return orderRepository.findByIdempotencyKey(command.idempotencyKey())
                .map(existing -> new OrderResult(
                        existing.getId(),
                        existing.getAccountId(),
                        existing.getSymbol(),
                        existing.getStatus(),
                        existing.getCreatedAt()
                ))
                .orElseGet(() -> createNewOrder(command));
    }

    private OrderResult createNewOrder(PlaceOrderCommand command) {
        AccountBalance balance = accountBalanceRepository.findById(command.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account balance not found"));

        if (command.side() == OrderSide.BUY) {
            BigDecimal reserveAmount = calculateReserveAmount(command);
            balance.reserveCash(reserveAmount);
        }

        Order order = Order.pending(
                command.accountId(),
                command.symbol(),
                command.side(),
                command.orderType(),
                command.quantity(),
                command.price(),
                command.idempotencyKey()
        );

        order.accept();
        orderRepository.save(order);

        outboxEventRepository.save(OutboxEvent.pending(
                "ORDER",
                order.getId().toString(),
                "OrderAccepted",
                serializePayload(Map.of(
                        "orderId", order.getId(),
                        "accountId", order.getAccountId(),
                        "symbol", order.getSymbol(),
                        "status", order.getStatus().name()
                ))
        ));

        return new OrderResult(
                order.getId(),
                order.getAccountId(),
                order.getSymbol(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    private BigDecimal calculateReserveAmount(PlaceOrderCommand command) {
        if (command.price() == null) {
            throw new IllegalArgumentException("Buy order requires a price in this initial version");
        }
        return command.price().multiply(command.quantity());
    }

    private String serializePayload(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize outbox payload", exception);
        }
    }
}
