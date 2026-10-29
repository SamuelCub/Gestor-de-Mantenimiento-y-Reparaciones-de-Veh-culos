/**
 * AutoGest Pro - Aplicación Frontend Principal (Vanilla JS ES6+)
 */

import { fetchReparaciones, createReparacion, patchEstadoReparacion, fetchVehiculo } from './api.js';

// Estado local de la aplicación
const state = {
    reparaciones: [],
    filtro: 'TODOS',
    busqueda: '',
    isLoading: false
};

// Referencias del DOM
const elements = {
    ordersContainer: document.getElementById('orders-container'),
    emptyState: document.getElementById('empty-state'),
    loadingSpinner: document.getElementById('loading-spinner'),
    btnRefresh: document.getElementById('btn-refresh'),
    searchInput: document.getElementById('search-input'),
    filterChips: document.querySelectorAll('.filter-chip'),

    // Métricas
    metricTotal: document.getElementById('metric-total'),
    metricPendientes: document.getElementById('metric-pendientes'),
    metricEnProceso: document.getElementById('metric-en-proceso'),
    metricTerminadas: document.getElementById('metric-terminadas'),
    metricCoste: document.getElementById('metric-coste'),

    // Contadores en chips
    countTodos: document.getElementById('count-todos'),
    countPendientes: document.getElementById('count-pendientes'),
    countProceso: document.getElementById('count-proceso'),
    countTerminadas: document.getElementById('count-terminadas'),

    // Modal & Form
    modal: document.getElementById('modal-new-repair'),
    btnOpenModal: document.getElementById('btn-open-modal'),
    btnCloseModal: document.getElementById('btn-close-modal'),
    btnCancelModal: document.getElementById('btn-cancel-modal'),
    formReparacion: document.getElementById('form-reparacion'),
    btnSubmit: document.getElementById('btn-submit-repair'),

    // Campos formulario
    inputMatricula: document.getElementById('input-matricula'),
    btnCheckVehiculo: document.getElementById('btn-check-vehiculo'),
    vehiculoBadge: document.getElementById('vehiculo-status-badge'),
    seccionVehiculoNuevo: document.getElementById('seccion-vehiculo-nuevo'),
    inputFecha: document.getElementById('input-fecha'),
    inputDescripcion: document.getElementById('input-descripcion'),
    inputCoste: document.getElementById('input-coste'),
    inputEstado: document.getElementById('input-estado'),

    // Campos vehículo nuevo
    inputMarca: document.getElementById('input-marca'),
    inputModelo: document.getElementById('input-modelo'),
    inputAnio: document.getElementById('input-anio'),
    inputClienteNombre: document.getElementById('input-cliente-nombre'),
    inputClienteTelefono: document.getElementById('input-cliente-telefono'),
    inputClienteEmail: document.getElementById('input-cliente-email'),

    // Toasts
    toastContainer: document.getElementById('toast-container')
};

/**
 * Inicialización al cargar el DOM
 */
document.addEventListener('DOMContentLoaded', () => {
    initDefaultDate();
    setupEventListeners();
    cargarReparaciones();
});

/**
 * Establece la fecha actual en el campo de fecha
 */
function initDefaultDate() {
    const today = new Date().toISOString().split('T')[0];
    if (elements.inputFecha) {
        elements.inputFecha.value = today;
    }
}

/**
 * Configuración de eventos
 */
function setupEventListeners() {
    // Recargar datos
    elements.btnRefresh.addEventListener('click', cargarReparaciones);

    // Búsqueda en tiempo real
    elements.searchInput.addEventListener('input', (e) => {
        state.busqueda = e.target.value.toLowerCase().trim();
        renderOrders();
    });

    // Filtros por estado
    elements.filterChips.forEach(chip => {
        chip.addEventListener('click', () => {
            elements.filterChips.forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
            state.filtro = chip.dataset.filter;
            renderOrders();
        });
    });

    // Modal abrir/cerrar
    elements.btnOpenModal.addEventListener('click', abrirModal);
    elements.btnCloseModal.addEventListener('click', cerrarModal);
    elements.btnCancelModal.addEventListener('click', cerrarModal);
    elements.modal.addEventListener('click', (e) => {
        if (e.target === elements.modal) cerrarModal();
    });

    // Comprobación de vehículo por matrícula
    elements.btnCheckVehiculo.addEventListener('click', comprobarVehiculo);
    elements.inputMatricula.addEventListener('blur', () => {
        if (elements.inputMatricula.value.trim().length >= 4) {
            comprobarVehiculo();
        }
    });

    // Envío del formulario
    elements.formReparacion.addEventListener('submit', handleFormSubmit);
}

/**
 * Carga de datos desde la API REST
 */
async function cargarReparaciones() {
    setLoading(true);
    try {
        const data = await fetchReparaciones();
        state.reparaciones = data;
        actualizarMetricas();
        renderOrders();
        showToast('Lista de reparaciones sincronizada con éxito', 'info');
    } catch (error) {
        console.error('Error al cargar reparaciones:', error);
        showToast(`Error al cargar datos: ${error.message}`, 'error');
    } finally {
        setLoading(false);
    }
}

/**
 * Actualiza las métricas y contadores de la cabecera
 */
function actualizarMetricas() {
    const list = state.reparaciones;
    const total = list.length;
    const pendientes = list.filter(r => r.estado === 'PENDIENTE').length;
    const enProceso = list.filter(r => r.estado === 'EN_PROCESO').length;
    const terminadas = list.filter(r => r.estado === 'TERMINADO').length;

    const totalFacturacion = list.reduce((acc, r) => acc + (parseFloat(r.coste) || 0), 0);

    // Contadores en dashboard
    elements.metricTotal.textContent = total;
    elements.metricPendientes.textContent = pendientes;
    elements.metricEnProceso.textContent = enProceso;
    elements.metricTerminadas.textContent = terminadas;
    elements.metricCoste.textContent = `${totalFacturacion.toLocaleString('es-ES', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} €`;

    // Contadores en chips
    elements.countTodos.textContent = total;
    elements.countPendientes.textContent = pendientes;
    elements.countProceso.textContent = enProceso;
    elements.countTerminadas.textContent = terminadas;
}

/**
 * Filtra y renderiza las tarjetas de reparaciones
 */
function renderOrders() {
    let filtradas = state.reparaciones;

    // Filtro por estado
    if (state.filtro !== 'TODOS') {
        filtradas = filtradas.filter(r => r.estado === state.filtro);
    }

    // Filtro por búsqueda de texto
    if (state.busqueda) {
        filtradas = filtradas.filter(r => {
            const matricula = (r.matricula || '').toLowerCase();
            const marca = (r.marca || '').toLowerCase();
            const modelo = (r.modelo || '').toLowerCase();
            const cliente = (r.clienteNombre || '').toLowerCase();
            const desc = (r.descripcion || '').toLowerCase();
            return matricula.includes(state.busqueda) ||
                   marca.includes(state.busqueda) ||
                   modelo.includes(state.busqueda) ||
                   cliente.includes(state.busqueda) ||
                   desc.includes(state.busqueda);
        });
    }

    elements.ordersContainer.innerHTML = '';

    if (filtradas.length === 0) {
        elements.emptyState.classList.remove('hidden');
        return;
    }

    elements.emptyState.classList.add('hidden');

    filtradas.forEach(repair => {
        const card = createRepairCard(repair);
        elements.ordersContainer.appendChild(card);
    });
}

/**
 * Genera el elemento DOM para cada tarjeta de reparación
 */
function createRepairCard(repair) {
    const card = document.createElement('article');
    const estadoClass = repair.estado.toLowerCase().replace('_', '-');
    card.className = `repair-card status-${estadoClass}`;
    card.dataset.id = repair.id;

    const formatearFecha = (fechaStr) => {
        if (!fechaStr) return 'N/D';
        const [year, month, day] = fechaStr.split('-');
        return `${day}/${month}/${year}`;
    };

    const formatearCoste = (coste) => {
        const num = parseFloat(coste) || 0;
        return `${num.toLocaleString('es-ES', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} €`;
    };

    card.innerHTML = `
        <div>
            <div class="card-header-row">
                <div>
                    <span class="plate-badge">${escapeHtml(repair.matricula)}</span>
                    <div class="vehicle-desc">${escapeHtml(repair.marca || '')} ${escapeHtml(repair.modelo || '')} ${repair.anio ? `(${repair.anio})` : ''}</div>
                </div>
                <div class="status-selector-wrap">
                    <select class="status-select state-${estadoClass}" data-repair-id="${repair.id}" title="Cambiar estado">
                        <option value="PENDIENTE" ${repair.estado === 'PENDIENTE' ? 'selected' : ''}>🟡 PENDIENTE</option>
                        <option value="EN_PROCESO" ${repair.estado === 'EN_PROCESO' ? 'selected' : ''}>🔵 EN PROCESO</option>
                        <option value="TERMINADO" ${repair.estado === 'TERMINADO' ? 'selected' : ''}>🟢 TERMINADO</option>
                    </select>
                </div>
            </div>

            <div class="card-body" style="margin-top: 0.85rem;">
                <p class="repair-description">${escapeHtml(repair.descripcion)}</p>
                <div class="client-info">
                    <div class="client-row">
                        <span>👤</span> <strong>${escapeHtml(repair.clienteNombre || 'Sin cliente')}</strong>
                    </div>
                    <div class="client-row">
                        <span>📞</span> <span>${escapeHtml(repair.clienteTelefono || 'Sin teléfono')}</span>
                    </div>
                    <div class="client-row">
                        <span>✉️</span> <span>${escapeHtml(repair.clienteEmail || 'Sin email')}</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="card-footer">
            <div class="repair-date">
                <span>Ingreso:</span> <strong>${formatearFecha(repair.fechaIngreso)}</strong>
            </div>
            <div class="repair-cost">${formatearCoste(repair.coste)}</div>
        </div>
    `;

    // Selector interactivo con actualización en tiempo real mediante PATCH
    const select = card.querySelector('.status-select');
    select.addEventListener('change', async (e) => {
        const nuevoEstado = e.target.value;
        const previousEstado = repair.estado;
        try {
            select.disabled = true;
            await patchEstadoReparacion(repair.id, nuevoEstado);
            
            // Actualizar objeto en estado local
            repair.estado = nuevoEstado;
            const nuevaClase = nuevoEstado.toLowerCase().replace('_', '-');
            
            // Actualizar clases de la tarjeta y del selector
            card.className = `repair-card status-${nuevaClase}`;
            select.className = `status-select state-${nuevaClase}`;
            
            actualizarMetricas();
            showToast(`Orden #${repair.id} actualizada a: ${nuevoEstado}`, 'success');
        } catch (error) {
            console.error('Error al actualizar estado:', error);
            // Revertir selección en caso de fallo
            select.value = previousEstado;
            showToast(`Error: ${error.message}`, 'error');
        } finally {
            select.disabled = false;
        }
    });

    return card;
}

/**
 * Comprueba si la matrícula ya existe en la base de datos
 */
async function comprobarVehiculo() {
    const matricula = elements.inputMatricula.value.trim().toUpperCase();
    if (!matricula) {
        showToast('Escriba una matrícula primero', 'info');
        return;
    }

    try {
        elements.vehiculoBadge.classList.remove('hidden', 'found', 'not-found');
        elements.vehiculoBadge.textContent = 'Verificando...';

        const vehiculo = await fetchVehiculo(matricula);
        // Si existe
        elements.vehiculoBadge.className = 'hint-badge found';
        elements.vehiculoBadge.textContent = `✓ Registrado: ${vehiculo.marca} ${vehiculo.modelo} (${vehiculo.clienteNombre})`;
        elements.seccionVehiculoNuevo.classList.add('hidden');
    } catch (error) {
        // Si no existe (404)
        if (error.status === 404) {
            elements.vehiculoBadge.className = 'hint-badge not-found';
            elements.vehiculoBadge.textContent = 'ℹ Vehículo nuevo. Rellena los datos a continuación:';
            elements.seccionVehiculoNuevo.classList.remove('hidden');
        } else {
            showToast(`Error al consultar: ${error.message}`, 'error');
        }
    }
}

/**
 * Envío del formulario de alta de reparación
 */
async function handleFormSubmit(e) {
    e.preventDefault();

    const matricula = elements.inputMatricula.value.trim().toUpperCase();
    const descripcion = elements.inputDescripcion.value.trim();
    const coste = parseFloat(elements.inputCoste.value);
    const estado = elements.inputEstado.value;
    const fechaIngreso = elements.inputFecha.value;

    if (!matricula || !descripcion || isNaN(coste)) {
        showToast('Por favor completa todos los campos obligatorios (*)', 'error');
        return;
    }

    const payload = {
        vehiculoMatricula: matricula,
        descripcion,
        coste,
        estado,
        fechaIngreso
    };

    // Si los campos de vehículo nuevo están visibles, añadirlos al payload
    if (!elements.seccionVehiculoNuevo.classList.contains('hidden')) {
        payload.vehiculoMarca = elements.inputMarca.value.trim();
        payload.vehiculoModelo = elements.inputModelo.value.trim();
        payload.vehiculoAnio = parseInt(elements.inputAnio.value) || new Date().getFullYear();
        payload.clienteNombre = elements.inputClienteNombre.value.trim();
        payload.clienteTelefono = elements.inputClienteTelefono.value.trim();
        payload.clienteEmail = elements.inputClienteEmail.value.trim();

        if (!payload.vehiculoMarca || !payload.vehiculoModelo || !payload.clienteNombre) {
            showToast('Para un vehículo nuevo, la marca, modelo y nombre de cliente son obligatorios', 'error');
            return;
        }
    }

    try {
        setSubmitLoading(true);
        await createReparacion(payload);
        showToast('¡Orden de trabajo creada exitosamente!', 'success');
        cerrarModal();
        await cargarReparaciones();
    } catch (error) {
        console.error('Error al registrar reparación:', error);
        showToast(`Error al registrar orden: ${error.message}`, 'error');
    } finally {
        setSubmitLoading(false);
    }
}

/**
 * Control del Modal
 */
function abrirModal() {
    elements.formReparacion.reset();
    initDefaultDate();
    elements.vehiculoBadge.classList.add('hidden');
    elements.seccionVehiculoNuevo.classList.add('hidden');
    elements.modal.classList.remove('hidden');
    elements.inputMatricula.focus();
}

function cerrarModal() {
    elements.modal.classList.add('hidden');
}

/**
 * Helpers UI
 */
function setLoading(loading) {
    state.isLoading = loading;
    if (loading) {
        elements.loadingSpinner.classList.remove('hidden');
    } else {
        elements.loadingSpinner.classList.add('hidden');
    }
}

function setSubmitLoading(loading) {
    const spinner = elements.btnSubmit.querySelector('.btn-spinner');
    if (loading) {
        elements.btnSubmit.disabled = true;
        spinner.classList.remove('hidden');
    } else {
        elements.btnSubmit.disabled = false;
        spinner.classList.add('hidden');
    }
}

function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    let icon = 'ℹ️';
    if (type === 'success') icon = '✅';
    if (type === 'error') icon = '⚠️';

    toast.innerHTML = `<span>${icon}</span> <span>${escapeHtml(message)}</span>`;
    elements.toastContainer.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
