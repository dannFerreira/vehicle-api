package com.postech.vehicleapi.dto;

import com.postech.vehicleapi.entity.Vehicle;
import com.postech.vehicleapi.entity.VehicleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VehicleResponse(
        Long id,
        String brand,
        String model,
        Integer year,
        String color,
        BigDecimal price,
        VehicleStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static VehicleResponse fromEntity(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getColor(),
                vehicle.getPrice(),
                vehicle.getStatus(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }
}