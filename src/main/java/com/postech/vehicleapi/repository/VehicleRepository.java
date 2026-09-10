package com.postech.vehicleapi.repository;

import com.postech.vehicleapi.entity.Vehicle;
import com.postech.vehicleapi.entity.VehicleStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByStatusOrderByPriceAsc(VehicleStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Vehicle> findByIdForUpdate(Long id);
}