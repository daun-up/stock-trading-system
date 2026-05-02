package com.toybroker.trading.order.application;

import com.toybroker.trading.common.exception.DomainException;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class AccountBalanceNotFoundException extends DomainException {

    public AccountBalanceNotFoundException(UUID accountId) {
        super(HttpStatus.NOT_FOUND, "Account balance not found. accountId=" + accountId);
    }
}
