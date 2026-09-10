package com.postech.vehicleapi.controller;

import com.postech.vehicleapi.dto.PurchaseResponse;
import com.postech.vehicleapi.service.SaleService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicles")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/{id}/purchase")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseResponse purchase(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        String buyerId = jwt.getSubject();

        return saleService.purchase(id, buyerId);
    }
}