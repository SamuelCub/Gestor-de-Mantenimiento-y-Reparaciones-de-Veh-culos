package com.taller.gestor.service;

import com.taller.gestor.dao.VehiculoDao;
import com.taller.gestor.dto.VehiculoDetalleDto;
import com.taller.gestor.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService {

    private final VehiculoDao vehiculoDao;

    public VehiculoService(VehiculoDao vehiculoDao) {
        this.vehiculoDao = vehiculoDao;
    }

    public boolean existeVehiculo(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            return false;
        }
        return vehiculoDao.existsByMatricula(matricula.trim());
    }

    public VehiculoDetalleDto obtenerVehiculoPorMatricula(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("La matrícula no puede estar vacía");
        }
        return vehiculoDao.findDetalleByMatricula(matricula)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún vehículo con la matrícula: " + matricula));
    }
}
