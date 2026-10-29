package com.taller.gestor.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Reparacion {
    private Integer id;
    private String vehiculoMatricula;
    private String descripcion;
    private BigDecimal coste;
    private EstadoReparacion estado;
    private LocalDate fechaIngreso;

    public Reparacion() {
    }

    public Reparacion(Integer id, String vehiculoMatricula, String descripcion, BigDecimal coste, EstadoReparacion estado, LocalDate fechaIngreso) {
        this.id = id;
        this.vehiculoMatricula = vehiculoMatricula;
        this.descripcion = descripcion;
        this.coste = coste;
        this.estado = estado;
        this.fechaIngreso = fechaIngreso;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public EstadoReparacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoReparacion estado) {
        this.estado = estado;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    @Override
    public String toString() {
        return "Reparacion{" +
                "id=" + id +
                ", vehiculoMatricula='" + vehiculoMatricula + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", coste=" + coste +
                ", estado=" + estado +
                ", fechaIngreso=" + fechaIngreso +
                '}';
    }
}
