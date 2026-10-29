package com.taller.gestor.service;

import com.taller.gestor.dao.ClienteDao;
import com.taller.gestor.dao.ReparacionDao;
import com.taller.gestor.dao.VehiculoDao;
import com.taller.gestor.dto.ReparacionDetalleDto;
import com.taller.gestor.dto.ReparacionRegistroDto;
import com.taller.gestor.exception.BadRequestException;
import com.taller.gestor.exception.ResourceNotFoundException;
import com.taller.gestor.model.Cliente;
import com.taller.gestor.model.EstadoReparacion;
import com.taller.gestor.model.Reparacion;
import com.taller.gestor.model.Vehiculo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReparacionService {

    private final ReparacionDao reparacionDao;
    private final VehiculoDao vehiculoDao;
    private final ClienteDao clienteDao;

    public ReparacionService(ReparacionDao reparacionDao, VehiculoDao vehiculoDao, ClienteDao clienteDao) {
        this.reparacionDao = reparacionDao;
        this.vehiculoDao = vehiculoDao;
        this.clienteDao = clienteDao;
    }

    public List<ReparacionDetalleDto> listarReparaciones() {
        return reparacionDao.findAllDetalladas();
    }

    @Transactional
    public Reparacion registrarReparacion(ReparacionRegistroDto dto) {
        String matricula = dto.getVehiculoMatricula().trim().toUpperCase();

        // 1. Verificar si el vehículo ya existe en la base de datos
        if (!vehiculoDao.existsByMatricula(matricula)) {
            // Si no existe, comprobamos si se enviaron los datos necesarios para crearlo
            if (dto.getVehiculoMarca() == null || dto.getVehiculoMarca().isBlank() ||
                dto.getVehiculoModelo() == null || dto.getVehiculoModelo().isBlank() ||
                dto.getVehiculoAnio() == null ||
                dto.getClienteNombre() == null || dto.getClienteNombre().isBlank()) {
                throw new BadRequestException("El vehículo con matrícula '" + matricula + "' no existe. Por favor proporcione los datos del vehículo y cliente para registrarlo.");
            }

            // Comprobamos si el cliente ya existe por email o creamos uno nuevo
            Integer clienteId;
            String email = (dto.getClienteEmail() != null && !dto.getClienteEmail().isBlank()) 
                    ? dto.getClienteEmail().trim() 
                    : ("cliente_" + System.currentTimeMillis() + "@taller.local");

            var clienteExistente = clienteDao.findByEmail(email);
            if (clienteExistente.isPresent()) {
                clienteId = clienteExistente.get().getId();
            } else {
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setNombre(dto.getClienteNombre().trim());
                nuevoCliente.setTelefono(dto.getClienteTelefono() != null ? dto.getClienteTelefono().trim() : "N/D");
                nuevoCliente.setEmail(email);
                Cliente clienteGuardado = clienteDao.create(nuevoCliente);
                clienteId = clienteGuardado.getId();
            }

            // Creamos el vehículo
            Vehiculo nuevoVehiculo = new Vehiculo();
            nuevoVehiculo.setMatricula(matricula);
            nuevoVehiculo.setMarca(dto.getVehiculoMarca().trim());
            nuevoVehiculo.setModelo(dto.getVehiculoModelo().trim());
            nuevoVehiculo.setAnio(dto.getVehiculoAnio());
            nuevoVehiculo.setClienteId(clienteId);
            vehiculoDao.create(nuevoVehiculo);
        }

        // 2. Validar estado inicial
        EstadoReparacion estadoInicial = EstadoReparacion.PENDIENTE;
        if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
            if (!EstadoReparacion.esValido(dto.getEstado())) {
                throw new BadRequestException("Estado no válido: " + dto.getEstado() + ". Permitidos: PENDIENTE, EN_PROCESO, TERMINADO.");
            }
            estadoInicial = EstadoReparacion.valueOf(dto.getEstado().trim().toUpperCase());
        }

        LocalDate fechaIngreso = dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDate.now();

        // 3. Crear la orden de reparación
        Reparacion reparacion = new Reparacion();
        reparacion.setVehiculoMatricula(matricula);
        reparacion.setDescripcion(dto.getDescripcion().trim());
        reparacion.setCoste(dto.getCoste());
        reparacion.setEstado(estadoInicial);
        reparacion.setFechaIngreso(fechaIngreso);

        return reparacionDao.create(reparacion);
    }

    public Reparacion actualizarEstado(int id, String nuevoEstadoStr) {
        if (!EstadoReparacion.esValido(nuevoEstadoStr)) {
            throw new BadRequestException("Estado '" + nuevoEstadoStr + "' no válido. Estados válidos: PENDIENTE, EN_PROCESO, TERMINADO.");
        }

        EstadoReparacion nuevoEstado = EstadoReparacion.valueOf(nuevoEstadoStr.trim().toUpperCase());

        Reparacion reparacion = reparacionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ninguna reparación con el ID: " + id));

        reparacionDao.updateEstado(id, nuevoEstado);
        reparacion.setEstado(nuevoEstado);
        return reparacion;
    }
}
