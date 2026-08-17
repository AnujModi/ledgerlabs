package io.github.anujmodi.ledgerlab.accounts;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {
    public AccountSummary getSummary(UUID id) {
        return new AccountSummary(id, "Sample Account", new BigDecimal("1000.00"), "USD");
    }
}