package com.toybroker.trading.account.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "account_balances")
public class AccountBalance {

    @Id
    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "available_cash", nullable = false, precision = 19, scale = 4)
    private BigDecimal availableCash;

    @Column(name = "reserved_cash", nullable = false, precision = 19, scale = 4)
    private BigDecimal reservedCash;

    protected AccountBalance() {
    }

    public AccountBalance(UUID accountId, BigDecimal availableCash, BigDecimal reservedCash) {
        this.accountId = accountId;
        this.availableCash = availableCash;
        this.reservedCash = reservedCash;
    }

    public void reserveCash(BigDecimal amount) {
        if (availableCash.compareTo(amount) < 0) {
            throw new InsufficientCashException(availableCash, amount);
        }

        this.availableCash = this.availableCash.subtract(amount);
        this.reservedCash = this.reservedCash.add(amount);
    }
}
