package com.walletx.transferservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
                        .header("Authorization", "Bearer invalid-token")
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
}
