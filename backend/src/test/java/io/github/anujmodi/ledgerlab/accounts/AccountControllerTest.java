package io.github.anujmodi.ledgerlab.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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
}
