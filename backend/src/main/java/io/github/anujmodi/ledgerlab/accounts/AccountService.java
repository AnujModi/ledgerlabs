package io.github.anujmodi.ledgerlab.accounts;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    private static final List<AccountTransaction> TRANSACTIONS = List.of(
            new AccountTransaction(
                    UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                    "Payroll deposit",
                    new BigDecimal("2500.00"),
                    TransactionType.CREDIT,
                    LocalDate.of(2026, 8, 15)
            ),
            new AccountTransaction(
                    UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                    "Grocery store",
                    new BigDecimal("84.19"),
                    TransactionType.DEBIT,
                    LocalDate.of(2026, 8, 14)
            ),
            new AccountTransaction(
                    UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
                    "Electricity bill",
                    new BigDecimal("126.45"),
                    TransactionType.DEBIT,
                    LocalDate.of(2026, 8, 13)
            )
    );

    public AccountSummary getSummary(UUID id) {
        return new AccountSummary(id, "Sample Account", new BigDecimal("1000.00"), "USD");
    }

    public List<AccountTransaction> getTransactions(UUID accountId, TransactionType type) {
        return TRANSACTIONS.stream()
                .filter(transaction -> type == null || transaction.type() == type)
                .toList();
    }

    public AccountTransaction getTransaction(UUID accountId, UUID transactionId) {
        return TRANSACTIONS.stream()
                .filter(transaction -> transaction.id().equals(transactionId))
                .findFirst()
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }
}
