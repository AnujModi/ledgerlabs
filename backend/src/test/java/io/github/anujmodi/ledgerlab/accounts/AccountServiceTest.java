package io.github.anujmodi.ledgerlab.accounts;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void filtersTransactionsByTypeUsingStreamPipeline() {
        var accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        var transactions = accountService.getTransactions(accountId, TransactionType.DEBIT);

        assertEquals(2, transactions.size());
        assertTrue(transactions.stream()
                .allMatch(transaction -> transaction.type() == TransactionType.DEBIT));
    }

    @Test
    void returnsTransactionById() {
        var accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        var transactionId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        var transaction = accountService.getTransaction(accountId, transactionId);

        assertEquals(transactionId, transaction.id());
        assertEquals("Payroll deposit", transaction.description());
        assertEquals(new BigDecimal("2500.00"), transaction.amount());
        assertEquals(TransactionType.CREDIT, transaction.type());
        assertEquals(LocalDate.of(2026, 8, 15), transaction.bookedOn());
    }

    @Test
    void rejectsUnknownTransactionId() {
        var accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        var unknownTransactionId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

        var exception = assertThrows(TransactionNotFoundException.class,
                () -> accountService.getTransaction(accountId, unknownTransactionId));

        assertEquals(unknownTransactionId, exception.transactionId());
    }
}
