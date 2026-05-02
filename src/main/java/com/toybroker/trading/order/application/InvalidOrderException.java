package com.toybroker.trading.order.application;

import com.toybroker.trading.common.exception.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidOrderException extends DomainException {

    public InvalidOrderException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
