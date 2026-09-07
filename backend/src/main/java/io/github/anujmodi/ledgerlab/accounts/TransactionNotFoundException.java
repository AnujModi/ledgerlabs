package io.github.anujmodi.ledgerlab.accounts;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends RuntimeException {
    private final UUID transactionId;

    public TransactionNotFoundException(UUID transactionId) {
        super("Transaction not found: " + transactionId);
        this.transactionId = transactionId;
    }

    public UUID transactionId() {
        return transactionId;
    }
}
