package com.toybroker.trading.account.domain;

import com.toybroker.trading.common.exception.DomainException;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;

public class InsufficientCashException extends DomainException {

    public InsufficientCashException(BigDecimal availableCash, BigDecimal requiredCash) {
        super(
                HttpStatus.CONFLICT,
                "Insufficient available cash. available=" + availableCash + ", required=" + requiredCash
        );
    }
}
