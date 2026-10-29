package com.taller.gestor.controller;

import com.taller.gestor.dto.VehiculoDetalleDto;
import com.taller.gestor.service.VehiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints REST para la consulta y verificación de vehículos.
 */
@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    /**
     * GET /api/vehiculos/{matricula}
     * Verifica si el vehículo existe en el sistema y retorna sus datos junto con el cliente.
     */
    @GetMapping("/{matricula}")
    public ResponseEntity<VehiculoDetalleDto> obtenerVehiculo(@PathVariable String matricula) {
        VehiculoDetalleDto dto = vehiculoService.obtenerVehiculoPorMatricula(matricula);
        return ResponseEntity.ok(dto);
    }
}
