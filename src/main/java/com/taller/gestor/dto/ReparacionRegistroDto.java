package com.taller.gestor.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ReparacionRegistroDto {

    @NotBlank(message = "La matrícula del vehículo es obligatoria")
    private String vehiculoMatricula;

    @NotBlank(message = "La descripción de la avería/mantenimiento es obligatoria")
    private String descripcion;

    @NotNull(message = "El coste estimado o final es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El coste no puede ser negativo")
    private BigDecimal coste;

    private String estado = "PENDIENTE";

    private LocalDate fechaIngreso;

    // Campos opcionales por si el vehículo aún no existe y el usuario lo da de alta en el mismo flujo
    private String vehiculoMarca;
    private String vehiculoModelo;
    private Integer vehiculoAnio;
    private String clienteNombre;
    private String clienteTelefono;
    private String clienteEmail;

    public ReparacionRegistroDto() {
    }

    public String getVehiculoMatricula() {
        return vehiculoMatricula;
    }

    public void setVehiculoMatricula(String vehiculoMatricula) {
        this.vehiculoMatricula = vehiculoMatricula;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCoste() {
        return coste;
    }

    public void setCoste(BigDecimal coste) {
        this.coste = coste;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getVehiculoMarca() {
        return vehiculoMarca;
    }

    public void setVehiculoMarca(String vehiculoMarca) {
        this.vehiculoMarca = vehiculoMarca;
    }

    public String getVehiculoModelo() {
        return vehiculoModelo;
    }

    public void setVehiculoModelo(String vehiculoModelo) {
        this.vehiculoModelo = vehiculoModelo;
    }

    public Integer getVehiculoAnio() {
        return vehiculoAnio;
    }

    public void setVehiculoAnio(Integer vehiculoAnio) {
        this.vehiculoAnio = vehiculoAnio;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }
}
