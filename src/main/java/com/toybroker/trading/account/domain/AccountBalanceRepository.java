package com.toybroker.trading.account.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountBalanceRepository extends JpaRepository<AccountBalance, UUID> {
}
