package com.taller.gestor.controller;

import com.taller.gestor.dto.CambiarEstadoRequest;
import com.taller.gestor.dto.ReparacionDetalleDto;
import com.taller.gestor.dto.ReparacionRegistroDto;
import com.taller.gestor.model.Reparacion;
import com.taller.gestor.service.ReparacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST para la gestión de reparaciones y órdenes de trabajo del taller.
 */
@RestController
@RequestMapping("/api/reparaciones")
public class ReparacionController {

    private final ReparacionService reparacionService;

    public ReparacionController(ReparacionService reparacionService) {
        this.reparacionService = reparacionService;
    }

    /**
     * GET /api/reparaciones
     * Devuelve la lista completa de reparaciones con detalles del vehículo y cliente asociado.
     */
    @GetMapping
    public ResponseEntity<List<ReparacionDetalleDto>> listarReparaciones() {
        List<ReparacionDetalleDto> listado = reparacionService.listarReparaciones();
        return ResponseEntity.ok(listado);
    }

    /**
     * POST /api/reparaciones
     * Registra una nueva orden de reparación.
     */
    @PostMapping
    public ResponseEntity<Reparacion> registrarReparacion(@Valid @RequestBody ReparacionRegistroDto dto) {
        Reparacion nuevaReparacion = reparacionService.registrarReparacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReparacion);
    }

    /**
     * PATCH /api/reparaciones/{id}/estado
     * Modifica el estado de una reparación entre 'PENDIENTE', 'EN_PROCESO' y 'TERMINADO'.
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Reparacion> cambiarEstado(
            @PathVariable int id,
            @Valid @RequestBody CambiarEstadoRequest request) {
        Reparacion reparacionActualizada = reparacionService.actualizarEstado(id, request.getEstado());
        return ResponseEntity.ok(reparacionActualizada);
    }
}
