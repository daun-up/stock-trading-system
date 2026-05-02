package com.toybroker.trading.order.presentation;

import com.toybroker.trading.common.api.ApiResponse;
import com.toybroker.trading.order.application.OrderResult;
import com.toybroker.trading.order.application.OrderService;
import com.toybroker.trading.order.application.command.PlaceOrderCommand;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderResult> placeOrder(@Valid @RequestBody PlaceOrderCommand command) {
        return ApiResponse.ok(orderService.placeOrder(command));
    }
}
