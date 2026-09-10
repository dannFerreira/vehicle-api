package com.postech.vehicleapi.service;

import com.postech.vehicleapi.dto.CreateVehicleRequest;
import com.postech.vehicleapi.dto.UpdateVehicleRequest;
import com.postech.vehicleapi.dto.VehicleResponse;
import com.postech.vehicleapi.entity.Vehicle;
import com.postech.vehicleapi.entity.VehicleStatus;
import com.postech.vehicleapi.exception.VehicleNotFoundException;
import com.postech.vehicleapi.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void shouldCreateVehicle() {
        CreateVehicleRequest request = new CreateVehicleRequest(
                "Toyota",
                "Corolla",
                2025,
                "Preto",
                new BigDecimal("120000.00")
        );

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(1L);
        savedVehicle.setBrand("Toyota");
        savedVehicle.setModel("Corolla");
        savedVehicle.setYear(2025);
        savedVehicle.setColor("Preto");
        savedVehicle.setPrice(new BigDecimal("120000.00"));

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(savedVehicle);

        VehicleResponse response = vehicleService.create(request);

        assertEquals(1L, response.id());
        assertEquals("Toyota", response.brand());
        assertEquals("Corolla", response.model());
        assertEquals(2025, response.year());
        assertEquals("Preto", response.color());
        assertEquals(new BigDecimal("120000.00"), response.price());

        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void shouldListAvailableVehiclesOrderedByPrice() {
        Vehicle cheaperVehicle = new Vehicle();
        cheaperVehicle.setId(1L);
        cheaperVehicle.setBrand("Toyota");
        cheaperVehicle.setModel("Corolla");
        cheaperVehicle.setYear(2025);
        cheaperVehicle.setColor("Preto");
        cheaperVehicle.setPrice(new BigDecimal("80000.00"));

        Vehicle expensiveVehicle = new Vehicle();
        expensiveVehicle.setId(2L);
        expensiveVehicle.setBrand("Honda");
        expensiveVehicle.setModel("Civic");
        expensiveVehicle.setYear(2025);
        expensiveVehicle.setColor("Branco");
        expensiveVehicle.setPrice(new BigDecimal("120000.00"));

        when(vehicleRepository.findByStatusOrderByPriceAsc(VehicleStatus.AVAILABLE))
                .thenReturn(List.of(cheaperVehicle, expensiveVehicle));

        List<VehicleResponse> response = vehicleService.findAvailable();

        assertEquals(2, response.size());
        assertEquals(new BigDecimal("80000.00"), response.get(0).price());
        assertEquals(new BigDecimal("120000.00"), response.get(1).price());

        verify(vehicleRepository)
                .findByStatusOrderByPriceAsc(VehicleStatus.AVAILABLE);
    }

    @Test
    void shouldUpdateVehicle() {
        UpdateVehicleRequest request = new UpdateVehicleRequest(
                "Toyota",
                "Corolla XEI",
                2025,
                "Prata",
                new BigDecimal("125000.00")
        );

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setBrand("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setYear(2024);
        vehicle.setColor("Preto");
        vehicle.setPrice(new BigDecimal("120000.00"));
        vehicle.setStatus(VehicleStatus.AVAILABLE);

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        VehicleResponse response = vehicleService.update(1L, request);

        assertEquals(1L, response.id());
        assertEquals("Toyota", response.brand());
        assertEquals("Corolla XEI", response.model());
        assertEquals(2025, response.year());
        assertEquals("Prata", response.color());
        assertEquals(new BigDecimal("125000.00"), response.price());

        verify(vehicleRepository).findById(1L);
    }

    @Test
    void shouldListSoldVehiclesOrderedByPrice() {
        Vehicle cheaperVehicle = new Vehicle();
        cheaperVehicle.setId(1L);
        cheaperVehicle.setBrand("Toyota");
        cheaperVehicle.setModel("Corolla");
        cheaperVehicle.setYear(2025);
        cheaperVehicle.setColor("Preto");
        cheaperVehicle.setPrice(new BigDecimal("80000.00"));
        cheaperVehicle.setStatus(VehicleStatus.SOLD);

        Vehicle expensiveVehicle = new Vehicle();
        expensiveVehicle.setId(2L);
        expensiveVehicle.setBrand("Honda");
        expensiveVehicle.setModel("Civic");
        expensiveVehicle.setYear(2025);
        expensiveVehicle.setColor("Branco");
        expensiveVehicle.setPrice(new BigDecimal("120000.00"));
        expensiveVehicle.setStatus(VehicleStatus.SOLD);

        when(vehicleRepository.findByStatusOrderByPriceAsc(VehicleStatus.SOLD))
                .thenReturn(List.of(cheaperVehicle, expensiveVehicle));

        List<VehicleResponse> response = vehicleService.findSold();

        assertEquals(2, response.size());
        assertEquals(new BigDecimal("80000.00"), response.get(0).price());
        assertEquals(new BigDecimal("120000.00"), response.get(1).price());

        assertEquals(VehicleStatus.SOLD, response.get(0).status());
        assertEquals(VehicleStatus.SOLD, response.get(1).status());

        verify(vehicleRepository)
                .findByStatusOrderByPriceAsc(VehicleStatus.SOLD);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingVehicle() {
        UpdateVehicleRequest request = new UpdateVehicleRequest(
                "Toyota", "Corolla", 2025, "Preto", new BigDecimal("120000.00")
        );

        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                VehicleNotFoundException.class,
                () -> vehicleService.update(999L, request)
        );

        verify(vehicleRepository).findById(999L);
    }
}