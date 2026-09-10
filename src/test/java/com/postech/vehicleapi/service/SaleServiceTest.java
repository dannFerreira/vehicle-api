package com.postech.vehicleapi.service;

import com.postech.vehicleapi.dto.PurchaseResponse;
import com.postech.vehicleapi.entity.Sale;
import com.postech.vehicleapi.entity.Vehicle;
import com.postech.vehicleapi.entity.VehicleStatus;
import com.postech.vehicleapi.exception.VehicleAlreadySoldException;
import com.postech.vehicleapi.exception.VehicleNotFoundException;
import com.postech.vehicleapi.repository.SaleRepository;
import com.postech.vehicleapi.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private SaleService saleService;

    @Test
    void shouldPurchaseAvailableVehicle() {
        Vehicle vehicle = createVehicle(
                1L,
                VehicleStatus.AVAILABLE,
                new BigDecimal("100000.00")
        );

        when(vehicleRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(vehicle));

        Sale savedSale = new Sale();
        savedSale.setId(10L);
        savedSale.setVehicleId(1L);
        savedSale.setBuyerId("buyer-123");
        savedSale.setPrice(new BigDecimal("100000.00"));

        when(saleRepository.save(any(Sale.class)))
                .thenReturn(savedSale);

        PurchaseResponse response =
                saleService.purchase(1L, "buyer-123");

        assertEquals(10L, response.saleId());
        assertEquals(1L, response.vehicleId());
        assertEquals("buyer-123", response.buyerId());
        assertEquals(new BigDecimal("100000.00"), response.price());

        assertEquals(VehicleStatus.SOLD, vehicle.getStatus());

        verify(vehicleRepository).findByIdForUpdate(1L);
        verify(saleRepository).save(any(Sale.class));
    }

    @Test
    void shouldNotPurchaseAlreadySoldVehicle() {
        Vehicle vehicle = createVehicle(
                1L,
                VehicleStatus.SOLD,
                new BigDecimal("100000.00")
        );

        when(vehicleRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(vehicle));

        assertThrows(
                VehicleAlreadySoldException.class,
                () -> saleService.purchase(1L, "buyer-123")
        );

        verify(vehicleRepository).findByIdForUpdate(1L);
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    void shouldSaveSaleWithVehiclePrice() {
        Vehicle vehicle = createVehicle(
                1L,
                VehicleStatus.AVAILABLE,
                new BigDecimal("95000.00")
        );

        when(vehicleRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(vehicle));

        when(saleRepository.save(any(Sale.class)))
                .thenAnswer(invocation -> {
                    Sale sale = invocation.getArgument(0);
                    sale.setId(20L);
                    return sale;
                });

        PurchaseResponse response =
                saleService.purchase(1L, "buyer-456");

        assertEquals(new BigDecimal("95000.00"), response.price());

        verify(saleRepository).save(argThat(sale ->
                sale.getVehicleId().equals(1L)
                        && sale.getBuyerId().equals("buyer-456")
                        && sale.getPrice().equals(new BigDecimal("95000.00"))
        ));
    }

    @Test
    void shouldThrowVehicleNotFoundExceptionWhenPurchasingNonExistingVehicle() {
        when(vehicleRepository.findByIdForUpdate(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                VehicleNotFoundException.class,
                () -> saleService.purchase(999L, "buyer-123")
        );

        verify(vehicleRepository).findByIdForUpdate(999L);
        verify(saleRepository, never()).save(any(Sale.class));
    }

    private Vehicle createVehicle(
            Long id,
            VehicleStatus status,
            BigDecimal price) {

        Vehicle vehicle = new Vehicle();

        vehicle.setId(id);
        vehicle.setBrand("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setYear(2025);
        vehicle.setColor("Preto");
        vehicle.setPrice(price);
        vehicle.setStatus(status);

        return vehicle;
    }
}