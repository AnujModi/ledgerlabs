package io.github.anujmodi.ledgerlab.accounts;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}/summary")
    public AccountSummary getAccountSummary(@PathVariable UUID accountId) {
        return accountService.getSummary(accountId);
    }

    @GetMapping("/{accountId}/transactions")
    public List<AccountTransaction> getTransactions(
            @PathVariable UUID accountId,
            @RequestParam(required = false) TransactionType type
    ) {
        return accountService.getTransactions(accountId, type);
    }

    @GetMapping("/{accountId}/transactions/{transactionId}")
    public AccountTransaction getTransaction(
            @PathVariable UUID accountId,
            @PathVariable UUID transactionId
    ) {
        return accountService.getTransaction(accountId, transactionId);
    }
}
