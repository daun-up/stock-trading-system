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
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        validateSupportedOrder(command);

        AccountBalance balance = accountBalanceRepository.findById(command.accountId())
                .orElseThrow(() -> new AccountBalanceNotFoundException(command.accountId()));

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

    private void validateSupportedOrder(PlaceOrderCommand command) {
        if (command.side() == OrderSide.SELL) {
            throw new InvalidOrderException("Sell order is not supported until positions are implemented");
        }
    }

    private BigDecimal calculateReserveAmount(PlaceOrderCommand command) {
        if (command.price() == null) {
            throw new InvalidOrderException("Buy order requires a price in this initial version");
        }
        if (command.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderException("Order price must be greater than zero");
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
