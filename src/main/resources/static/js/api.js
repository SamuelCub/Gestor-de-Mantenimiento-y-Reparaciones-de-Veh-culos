/**
 * Módulo de Comunicación con la API REST (Fetch API con async/await)
 */

const API_BASE_URL = ''; // Relativo al host actual (ej. http://localhost:8080)

/**
 * Función auxiliar para procesar respuestas de la API
 */
async function handleResponse(response) {
    if (!response.ok) {
        let errorMessage = `Error HTTP ${response.status}: ${response.statusText}`;
        try {
            const errorJson = await response.json();
            if (errorJson.message) {
                errorMessage = errorJson.message;
            }
        } catch (_) {
            // Si la respuesta no es JSON, se conserva el statusText
        }
        const error = new Error(errorMessage);
        error.status = response.status;
        throw error;
    }
    // Si la respuesta es 204 No Content
    if (response.status === 204) return null;
    return await response.json();
}

/**
 * GET /api/reparaciones
 * Obtiene la lista completa de reparaciones con vehículos y clientes
 */
export async function fetchReparaciones() {
    const response = await fetch(`${API_BASE_URL}/api/reparaciones`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    });
    return await handleResponse(response);
}

/**
 * POST /api/reparaciones
 * Registra una nueva orden de trabajo
 */
export async function createReparacion(reparacionData) {
    const response = await fetch(`${API_BASE_URL}/api/reparaciones`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify(reparacionData)
    });
    return await handleResponse(response);
}

/**
 * PATCH /api/reparaciones/{id}/estado
 * Actualiza el estado de la reparación de forma inmediata
 */
export async function patchEstadoReparacion(id, nuevoEstado) {
    const response = await fetch(`${API_BASE_URL}/api/reparaciones/${id}/estado`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify({ estado: nuevoEstado })
    });
    return await handleResponse(response);
}

/**
 * GET /api/vehiculos/{matricula}
 * Consulta los datos de un vehículo por matrícula
 */
export async function fetchVehiculo(matricula) {
    const response = await fetch(`${API_BASE_URL}/api/vehiculos/${encodeURIComponent(matricula.trim().toUpperCase())}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    });
    return await handleResponse(response);
}
