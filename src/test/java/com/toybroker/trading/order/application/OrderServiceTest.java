package com.toybroker.trading.order.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toybroker.trading.account.domain.AccountBalance;
import com.toybroker.trading.account.domain.AccountBalanceRepository;
import com.toybroker.trading.order.application.command.PlaceOrderCommand;
import com.toybroker.trading.order.domain.Order;
import com.toybroker.trading.order.domain.OrderRepository;
import com.toybroker.trading.order.domain.OrderSide;
import com.toybroker.trading.order.domain.OrderStatus;
import com.toybroker.trading.order.domain.OrderType;
import com.toybroker.trading.outbox.domain.OutboxEvent;
import com.toybroker.trading.outbox.domain.OutboxEventRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class OrderServiceTest {

    private final OrderRepository orderRepository = org.mockito.Mockito.mock(OrderRepository.class);
    private final AccountBalanceRepository accountBalanceRepository = org.mockito.Mockito.mock(AccountBalanceRepository.class);
    private final OutboxEventRepository outboxEventRepository = org.mockito.Mockito.mock(OutboxEventRepository.class);
    private final OrderService orderService = new OrderService(
            orderRepository,
            accountBalanceRepository,
            outboxEventRepository,
            new ObjectMapper()
    );

    @Test
    void placeOrderReturnsExistingOrderWhenIdempotencyKeyAlreadyExists() {
        UUID accountId = UUID.randomUUID();
        Order existingOrder = Order.pending(
                accountId,
                "005930",
                OrderSide.BUY,
                OrderType.LIMIT,
                BigDecimal.TEN,
                new BigDecimal("70000"),
                "same-key"
        );
        existingOrder.accept();

        when(orderRepository.findByIdempotencyKey("same-key")).thenReturn(Optional.of(existingOrder));

        OrderResult result = orderService.placeOrder(new PlaceOrderCommand(
                accountId,
                "005930",
                OrderSide.BUY,
                OrderType.LIMIT,
                BigDecimal.TEN,
                new BigDecimal("70000"),
                "same-key"
        ));

        assertThat(result.orderId()).isEqualTo(existingOrder.getId());
        assertThat(result.status()).isEqualTo(OrderStatus.ACCEPTED);
        verify(accountBalanceRepository, never()).findById(any());
        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    void placeOrderSavesAcceptedOrderAndOutboxEvent() {
        UUID accountId = UUID.randomUUID();
        AccountBalance balance = new AccountBalance(
                accountId,
                new BigDecimal("1000000.0000"),
                BigDecimal.ZERO
        );

        when(orderRepository.findByIdempotencyKey("order-key")).thenReturn(Optional.empty());
        when(accountBalanceRepository.findById(accountId)).thenReturn(Optional.of(balance));

        OrderResult result = orderService.placeOrder(new PlaceOrderCommand(
                accountId,
                "005930",
                OrderSide.BUY,
                OrderType.LIMIT,
                BigDecimal.TEN,
                new BigDecimal("70000"),
                "order-key"
        ));

        assertThat(result.status()).isEqualTo(OrderStatus.ACCEPTED);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.ACCEPTED);

        verify(outboxEventRepository).save(any(OutboxEvent.class));
    }

    @Test
    void placeOrderRejectsSellOrderUntilPositionsAreImplemented() {
        when(orderRepository.findByIdempotencyKey("sell-key")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.placeOrder(new PlaceOrderCommand(
                UUID.randomUUID(),
                "005930",
                OrderSide.SELL,
                OrderType.LIMIT,
                BigDecimal.TEN,
                new BigDecimal("70000"),
                "sell-key"
        )))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessageContaining("Sell order is not supported");

        verify(accountBalanceRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }
}
