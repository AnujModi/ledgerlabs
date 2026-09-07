package io.github.anujmodi.ledgerlab.accounts;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AccountTransaction(
        UUID id,
        String description,
        BigDecimal amount,
        TransactionType type,
        LocalDate bookedOn
) {
}
