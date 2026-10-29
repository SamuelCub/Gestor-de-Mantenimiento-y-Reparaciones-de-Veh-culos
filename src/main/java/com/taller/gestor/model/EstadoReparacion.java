package com.taller.gestor.model;

/**
 * Estados permitidos para una orden de reparación en el taller mecánico.
 */
public enum EstadoReparacion {
    PENDIENTE,
    EN_PROCESO,
    TERMINADO;

    public static boolean esValido(String estadoStr) {
        if (estadoStr == null) return false;
        try {
            EstadoReparacion.valueOf(estadoStr.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
