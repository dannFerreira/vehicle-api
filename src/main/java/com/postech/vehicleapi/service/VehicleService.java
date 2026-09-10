package com.postech.vehicleapi.service;

import com.postech.vehicleapi.dto.CreateVehicleRequest;
import com.postech.vehicleapi.dto.UpdateVehicleRequest;
import com.postech.vehicleapi.dto.VehicleResponse;
import com.postech.vehicleapi.entity.Vehicle;
import com.postech.vehicleapi.entity.VehicleStatus;
import com.postech.vehicleapi.exception.VehicleNotFoundException;
import com.postech.vehicleapi.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public VehicleResponse create(CreateVehicleRequest request) {
        Vehicle vehicle = new Vehicle();

        vehicle.setBrand(request.brand());
        vehicle.setModel(request.model());
        vehicle.setYear(request.year());
        vehicle.setColor(request.color());
        vehicle.setPrice(request.price());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return VehicleResponse.fromEntity(savedVehicle);
    }

    @Transactional
    public VehicleResponse update(Long id, UpdateVehicleRequest request) {
        Vehicle vehicle = findById(id);

        vehicle.setBrand(request.brand());
        vehicle.setModel(request.model());
        vehicle.setYear(request.year());
        vehicle.setColor(request.color());
        vehicle.setPrice(request.price());

        return VehicleResponse.fromEntity(vehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> findAvailable() {
        return vehicleRepository
                .findByStatusOrderByPriceAsc(VehicleStatus.AVAILABLE)
                .stream()
                .map(VehicleResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> findSold() {
        return vehicleRepository
                .findByStatusOrderByPriceAsc(VehicleStatus.SOLD)
                .stream()
                .map(VehicleResponse::fromEntity)
                .toList();
    }

    private Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(VehicleNotFoundException::new);
    }
}