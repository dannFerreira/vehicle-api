package com.postech.vehicleapi.repository;

import com.postech.vehicleapi.entity.Vehicle;
import com.postech.vehicleapi.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByStatusOrderByPriceAsc(VehicleStatus status);
}