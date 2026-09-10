package com.postech.vehicleapi.repository;

import com.postech.vehicleapi.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}