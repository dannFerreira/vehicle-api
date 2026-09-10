package com.postech.vehicleapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseResponse(
        Long saleId,
        Long vehicleId,
        String buyerId,
        BigDecimal price,
        LocalDateTime createdAt
) {
}