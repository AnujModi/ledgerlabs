package io.github.anujmodi.ledgerlab.accounts;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountServiceTest {
    private final AccountService accountService = new AccountService();

    @Test
    void returnsAccountSummary() {
        var accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        var summary = accountService.getSummary(accountId);

        assertEquals(accountId, summary.id());
        assertEquals("Sample Account", summary.displayName());
        assertEquals(new BigDecimal("1000.00"), summary.availableBalance());
        assertEquals("USD", summary.currency());
    }
}
