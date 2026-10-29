package com.taller.gestor.dto;

import jakarta.validation.constraints.NotBlank;

public class CambiarEstadoRequest {

    @NotBlank(message = "El nuevo estado es obligatorio")
    private String estado;

    public CambiarEstadoRequest() {
    }

    public CambiarEstadoRequest(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
