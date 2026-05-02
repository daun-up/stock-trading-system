package com.toybroker.trading.account.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountBalanceTest {

    @Test
    void reserveCashRejectsWhenAvailableCashIsInsufficient() {
        AccountBalance balance = new AccountBalance(
                UUID.randomUUID(),
                new BigDecimal("1000.0000"),
                BigDecimal.ZERO
        );

        assertThatThrownBy(() -> balance.reserveCash(new BigDecimal("1000.0001")))
                .isInstanceOf(InsufficientCashException.class)
                .hasMessageContaining("Insufficient available cash");
    }
}
