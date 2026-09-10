package com.postech.vehicleapi.controller;

import com.postech.vehicleapi.dto.CreateVehicleRequest;
import com.postech.vehicleapi.dto.UpdateVehicleRequest;
import com.postech.vehicleapi.dto.VehicleResponse;
import com.postech.vehicleapi.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse create(
            @Valid @RequestBody CreateVehicleRequest request) {

        return vehicleService.create(request);
    }

    @PutMapping("/{id}")
    public VehicleResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleRequest request) {

        return vehicleService.update(id, request);
    }

    @GetMapping
    public List<VehicleResponse> findAvailable() {
        return vehicleService.findAvailable();
    }

    @GetMapping("/sold")
    public List<VehicleResponse> findSold() {
        return vehicleService.findSold();
    }
}