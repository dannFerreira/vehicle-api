package com.postech.vehicleapi.controller;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.ObjectMapper;
import com.postech.vehicleapi.dto.CreateVehicleRequest;
import com.postech.vehicleapi.dto.UpdateVehicleRequest;
import com.postech.vehicleapi.dto.VehicleResponse;
import com.postech.vehicleapi.entity.VehicleStatus;
import com.postech.vehicleapi.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehicleService vehicleService;

    @Test
    void shouldCreateVehicle() throws Exception {
        CreateVehicleRequest request = new CreateVehicleRequest(
                "Toyota",
                "Corolla",
                2025,
                "Preto",
                new BigDecimal("120000.00")
        );

        VehicleResponse response = new VehicleResponse(
                1L,
                "Toyota",
                "Corolla",
                2025,
                "Preto",
                new BigDecimal("120000.00"),
                VehicleStatus.AVAILABLE,
                null,
                null
        );

        when(vehicleService.create(any(CreateVehicleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla"))
                .andExpect(jsonPath("$.price").value(120000.00));
    }

    @Test
    void shouldListAvailableVehicles() throws Exception {
        VehicleResponse vehicle = new VehicleResponse(
                1L,
                "Toyota",
                "Corolla",
                2025,
                "Preto",
                new BigDecimal("80000.00"),
                VehicleStatus.AVAILABLE,
                null,
                null
        );

        when(vehicleService.findAvailable())
                .thenReturn(List.of(vehicle));

        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                .andExpect(jsonPath("$[0].price").value(80000.00));
    }

    @Test
    void shouldListSoldVehicles() throws Exception {
        VehicleResponse vehicle = new VehicleResponse(
                1L,
                "Honda",
                "Civic",
                2025,
                "Branco",
                new BigDecimal("100000.00"),
                VehicleStatus.SOLD,
                null,
                null
        );

        when(vehicleService.findSold())
                .thenReturn(List.of(vehicle));

        mockMvc.perform(get("/vehicles/sold"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("SOLD"));
    }

    @Test
    void shouldUpdateVehicle() throws Exception {
        UpdateVehicleRequest request = new UpdateVehicleRequest(
                "Toyota",
                "Corolla XEI",
                2025,
                "Prata",
                new BigDecimal("125000.00")
        );

        VehicleResponse response = new VehicleResponse(
                1L,
                "Toyota",
                "Corolla XEI",
                2025,
                "Prata",
                new BigDecimal("125000.00"),
                VehicleStatus.AVAILABLE,
                null,
                null
        );

        when(vehicleService.update(eq(1L), any(UpdateVehicleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.model").value("Corolla XEI"))
                .andExpect(jsonPath("$.price").value(125000.00));
    }

    @Test
    void shouldRejectInvalidVehicleData() throws Exception {
        CreateVehicleRequest request = new CreateVehicleRequest(
                "",
                "",
                1800,
                "",
                BigDecimal.ZERO
        );

        mockMvc.perform(post("/vehicles")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}