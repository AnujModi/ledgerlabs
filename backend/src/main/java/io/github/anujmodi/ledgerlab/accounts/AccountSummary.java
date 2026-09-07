package io.github.anujmodi.ledgerlab.accounts;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountSummary(
        UUID id,
        String displayName,
        BigDecimal availableBalance,
        String currency
) {
}