package com.postech.vehicleapi.exception;

public class VehicleAlreadySoldException extends RuntimeException {

    public VehicleAlreadySoldException() {
        super("Vehicle already sold");
    }
}