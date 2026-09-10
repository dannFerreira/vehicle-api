package com.postech.vehicleapi.controller;

import com.postech.vehicleapi.dto.PurchaseResponse;
import com.postech.vehicleapi.service.SaleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleController.class)
@AutoConfigureMockMvc(addFilters = false)
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleService saleService;

    @Test
    void shouldPurchaseVehicle() throws Exception {
        PurchaseResponse response = new PurchaseResponse(
                10L,
                1L,
                "buyer-123",
                new BigDecimal("100000.00"),
                null
        );

        when(saleService.purchase(1L, "buyer-123"))
                .thenReturn(response);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("buyer-123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken(jwt, null, "ROLE_USER");

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        try {
            mockMvc.perform(post("/vehicles/1/purchase"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.saleId").value(10))
                    .andExpect(jsonPath("$.vehicleId").value(1))
                    .andExpect(jsonPath("$.buyerId").value("buyer-123"))
                    .andExpect(jsonPath("$.price").value(100000.00));

            verify(saleService).purchase(eq(1L), eq("buyer-123"));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
