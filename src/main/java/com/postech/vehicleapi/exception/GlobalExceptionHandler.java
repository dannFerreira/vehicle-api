package com.postech.vehicleapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VehicleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleVehicleNotFound(
            VehicleNotFoundException exception) {

        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(VehicleAlreadySoldException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleVehicleAlreadySold(
            VehicleAlreadySoldException exception) {

        return Map.of("error", exception.getMessage());
    }
}