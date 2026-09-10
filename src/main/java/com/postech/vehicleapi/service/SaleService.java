package com.postech.vehicleapi.service;

import com.postech.vehicleapi.dto.PurchaseResponse;
import com.postech.vehicleapi.entity.Sale;
import com.postech.vehicleapi.entity.Vehicle;
import com.postech.vehicleapi.entity.VehicleStatus;
import com.postech.vehicleapi.exception.VehicleAlreadySoldException;
import com.postech.vehicleapi.exception.VehicleNotFoundException;
import com.postech.vehicleapi.repository.SaleRepository;
import com.postech.vehicleapi.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleService {

    private final VehicleRepository vehicleRepository;
    private final SaleRepository saleRepository;

    public SaleService(
            VehicleRepository vehicleRepository,
            SaleRepository saleRepository) {
        this.vehicleRepository = vehicleRepository;
        this.saleRepository = saleRepository;
    }

    @Transactional
    public PurchaseResponse purchase(Long vehicleId, String buyerId) {

        Vehicle vehicle = vehicleRepository.findByIdForUpdate(vehicleId)
                .orElseThrow(VehicleNotFoundException::new);

        if (vehicle.getStatus() == VehicleStatus.SOLD) {
            throw new VehicleAlreadySoldException();
        }

        Sale sale = new Sale();
        sale.setVehicleId(vehicle.getId());
        sale.setBuyerId(buyerId);
        sale.setPrice(vehicle.getPrice());

        vehicle.setStatus(VehicleStatus.SOLD);

        Sale savedSale = saleRepository.save(sale);

        return new PurchaseResponse(
                savedSale.getId(),
                savedSale.getVehicleId(),
                savedSale.getBuyerId(),
                savedSale.getPrice(),
                savedSale.getCreatedAt()
        );
    }
}