package io.github.anujmodi.ledgerlab.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Test
    void returnsAccountSummaryAsJson() throws Exception {
        var accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        var summary = new AccountSummary(
                accountId,
                "Everyday Chequing",
                new BigDecimal("1000.00"),
                "CAD"
        );
        given(accountService.getSummary(accountId)).willReturn(summary);

        mockMvc.perform(get("/api/accounts/{accountId}/summary", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.displayName").value("Everyday Chequing"))
                .andExpect(jsonPath("$.availableBalance").value(1000.00))
                .andExpect(jsonPath("$.currency").value("CAD"));

        verify(accountService).getSummary(accountId);
    }

    @Test
    void rejectsMalformedAccountIdBeforeCallingService() throws Exception {
        mockMvc.perform(get("/api/accounts/not-a-uuid/summary"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void returnsDebitTransactionsAsJson() throws Exception {
        var accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        var transaction = new AccountTransaction(
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                "Grocery store",
                new BigDecimal("84.19"),
                TransactionType.DEBIT,
                LocalDate.of(2026, 8, 14)
        );
        given(accountService.getTransactions(accountId, TransactionType.DEBIT))
                .willReturn(List.of(transaction));

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", accountId)
                        .queryParam("type", "DEBIT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transaction.id().toString()))
                .andExpect(jsonPath("$[0].description").value("Grocery store"))
                .andExpect(jsonPath("$[0].amount").value(84.19))
                .andExpect(jsonPath("$[0].type").value("DEBIT"))
                .andExpect(jsonPath("$[0].bookedOn").value("2026-08-14"));

        verify(accountService).getTransactions(accountId, TransactionType.DEBIT);
    }

    @Test
    void returnsTransactionById() throws Exception {
        var accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        var transactionId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        var transaction = new AccountTransaction(
                transactionId,
                "Payroll deposit",
                new BigDecimal("2500.00"),
                TransactionType.CREDIT,
                LocalDate.of(2026, 8, 15)
        );
        given(accountService.getTransaction(accountId, transactionId)).willReturn(transaction);

        mockMvc.perform(get("/api/accounts/{accountId}/transactions/{transactionId}",
                        accountId, transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.type").value("CREDIT"));

        verify(accountService).getTransaction(accountId, transactionId);
    }

    @Test
    void returnsNotFoundForUnknownTransaction() throws Exception {
        var accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        var transactionId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
        given(accountService.getTransaction(accountId, transactionId))
                .willThrow(new TransactionNotFoundException(transactionId));

        mockMvc.perform(get("/api/accounts/{accountId}/transactions/{transactionId}",
                        accountId, transactionId))
                .andExpect(status().isNotFound());

        verify(accountService).getTransaction(accountId, transactionId);
    }
}
