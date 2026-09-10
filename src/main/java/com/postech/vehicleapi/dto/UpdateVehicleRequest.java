package com.postech.vehicleapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateVehicleRequest(

        @NotBlank
        String brand,

        @NotBlank
        String model,

        @NotNull
        @Min(1886)
        Integer year,

        @NotBlank
        String color,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal price
) {
}