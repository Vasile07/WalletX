package com.walletx.transferservice;

import com.walletx.transferservice.controller.TransferResponse;
import com.walletx.transferservice.domain.Transfer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransferServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void healthEndpointReturnsUpStatus() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"status":"UP","service":"transfer-service"}
                        """));
    }

    @Test
    void transferCreationRequiresAValidBearerToken() throws Exception {
        mockMvc.perform(post("/api/transfers")
                        .contentType("application/json")
                        .content("""
                                {
                                  "senderWalletId": "00000000-0000-0000-0000-000000000001",
                                  "receiverWalletId": "00000000-0000-0000-0000-000000000002",
                                  "amount": 10.00,
                                  "currency": "RON"
                                }
                                """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/transfers")
                        .header("Authorization", "******")
                        .contentType("application/json")
                        .content("""
                                {
                                  "senderWalletId": "00000000-0000-0000-0000-000000000001",
                                  "receiverWalletId": "00000000-0000-0000-0000-000000000002",
                                  "amount": 10.00,
                                  "currency": "RON"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void transferHistoryMarksSentAndReceivedDirections() {
        UUID userId = UUID.randomUUID();
        UUID myWalletOne = UUID.randomUUID();
        UUID myWalletTwo = UUID.randomUUID();
        UUID otherWallet = UUID.randomUUID();

        Transfer sentTransfer = new Transfer(myWalletOne, myWalletTwo, new BigDecimal("15.00"), "RON", "sent-key");
        TransferResponse sentResponse = TransferResponse.fromDomain(sentTransfer, userId, List.of(myWalletOne, myWalletTwo));

        assertEquals("SENT", sentResponse.getDirection());
        assertEquals(myWalletTwo, sentResponse.getCounterpartyWalletId());

        Transfer receivedTransfer = new Transfer(otherWallet, myWalletTwo, new BigDecimal("25.50"), "RON", "received-key");
        TransferResponse receivedResponse = TransferResponse.fromDomain(receivedTransfer, userId, List.of(myWalletOne, myWalletTwo));

        assertEquals("RECEIVED", receivedResponse.getDirection());
        assertEquals(otherWallet, receivedResponse.getCounterpartyWalletId());
    }
}
