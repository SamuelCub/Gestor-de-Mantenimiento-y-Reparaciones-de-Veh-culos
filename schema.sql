-- ========================================================
-- Base de Datos: Gestor de Taller Mecánico
-- Proyecto Portfolio DAM 2
-- Motor: MySQL 8.x / MariaDB
-- ========================================================

CREATE DATABASE IF NOT EXISTS taller_mecanico_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE taller_mecanico_db;

-- 1. Tabla de Clientes
CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Tabla de Vehículos
CREATE TABLE IF NOT EXISTS vehiculos (
    matricula VARCHAR(15) PRIMARY KEY,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    anio INT NOT NULL,
    cliente_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehiculos_clientes 
        FOREIGN KEY (cliente_id) REFERENCES clientes(id) 
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 3. Tabla de Reparaciones / Órdenes de Trabajo
CREATE TABLE IF NOT EXISTS reparaciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vehiculo_matricula VARCHAR(15) NOT NULL,
    descripcion TEXT NOT NULL,
    coste DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    estado ENUM('PENDIENTE', 'EN_PROCESO', 'TERMINADO') NOT NULL DEFAULT 'PENDIENTE',
    fecha_ingreso DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reparaciones_vehiculos 
        FOREIGN KEY (vehiculo_matricula) REFERENCES vehiculos(matricula) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ========================================================
-- Datos semilla (Seed Data) para pruebas y demostración
-- ========================================================

INSERT INTO clientes (id, nombre, telefono, email) VALUES
(1, 'Carlos García Martínez', '+34 612 345 678', 'carlos.garcia@email.com'),
(2, 'Laura Fernández Gómez', '+34 689 456 123', 'laura.fernandez@email.com'),
(3, 'Miguel Sánchez Rivas', '+34 654 987 321', 'miguel.sanchez@email.com')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO vehiculos (matricula, marca, modelo, anio, cliente_id) VALUES
('1234-KBC', 'Volkswagen', 'Golf VII 2.0 TDI', 2017, 1),
('5678-LMN', 'Toyota', 'Corolla Hybrid 1.8', 2020, 2),
('9012-HJK', 'Renault', 'Clio IV 0.9 TCe', 2015, 3)
ON DUPLICATE KEY UPDATE modelo = VALUES(modelo);

INSERT INTO reparaciones (vehiculo_matricula, descripcion, coste, estado, fecha_ingreso) VALUES
('1234-KBC', 'Cambio de pastillas y discos de freno delanteros + purgado de líquido de frenos.', 240.50, 'PENDIENTE', '2026-10-25'),
('5678-LMN', 'Revisión periódica de 60.000 km: cambio de aceite sintético, filtros de aire, polen y aceite.', 175.00, 'EN_PROCESO', '2026-10-26'),
('9012-HJK', 'Sustitución de kit de embrague y volante bimasa por desgaste.', 620.00, 'TERMINADO', '2026-10-22'),
('1234-KBC', 'Diagnosis electrónica por fallo en testigo de control de tracción (sensor ABS trasero).', 85.00, 'EN_PROCESO', '2026-10-27');
