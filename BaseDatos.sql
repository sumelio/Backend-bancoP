-- ============================================================================
-- Script de creación de bases de datos para el sistema de microservicios
-- ============================================================================

-- ============================================================================
-- Base de datos 1: clientesdb (MS-Clientes)
-- ============================================================================

CREATE DATABASE clientesdb;

\c clientesdb;

-- Tabla: persona
CREATE TABLE persona (
    id                   BIGSERIAL PRIMARY KEY,
    nombre               VARCHAR(255) NOT NULL,
    genero               VARCHAR(50),
    edad                 INT,
    identificacion       VARCHAR(50) UNIQUE NOT NULL,
    direccion            VARCHAR(255),
    telefono             VARCHAR(50),
    fecha_creacion       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   TIMESTAMP WITH TIME ZONE
);

-- Tabla: cliente
CREATE TABLE cliente (
    id                   BIGINT PRIMARY KEY,
    cliente_id           VARCHAR(50) UNIQUE NOT NULL,
    contrasena           VARCHAR(255) NOT NULL,
    estado               BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_cliente_persona FOREIGN KEY (id) REFERENCES persona(id) ON DELETE CASCADE
);

-- Índices adicionales para mejorar rendimiento
CREATE INDEX idx_persona_identificacion ON persona(identificacion);
CREATE INDEX idx_cliente_cliente_id ON cliente(cliente_id);

-- ============================================================================
-- Base de datos 2: cuentasdb (MS-Cuentas)
-- ============================================================================

CREATE DATABASE cuentasdb;

\c cuentasdb;

-- Tabla: cuenta
CREATE TABLE cuenta (
    id                   BIGSERIAL PRIMARY KEY,
    numero_cuenta        VARCHAR(50) UNIQUE NOT NULL,
    tipo                 VARCHAR(50) NOT NULL CHECK (tipo IN ('Ahorros', 'Corriente')),
    saldo_inicial        NUMERIC(15,2) NOT NULL DEFAULT 0,
    saldo_disponible     NUMERIC(15,2) NOT NULL DEFAULT 0,
    estado               BOOLEAN NOT NULL DEFAULT true,
    cliente_id           VARCHAR(50) NOT NULL,
    fecha_creacion       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   TIMESTAMP WITH TIME ZONE
);

-- Tabla: movimiento
CREATE TABLE movimiento (
    id                   BIGSERIAL PRIMARY KEY,
    fecha                TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo                 VARCHAR(50) NOT NULL CHECK (tipo IN ('Deposito', 'Retiro')),
    valor                NUMERIC(15,2) NOT NULL,
    saldo                NUMERIC(15,2) NOT NULL,
    cuenta_id            BIGINT NOT NULL,
    fecha_creacion       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_movimiento_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuenta(id) ON DELETE CASCADE
);

-- Tabla: cliente_local
CREATE TABLE cliente_local (
    cliente_id           VARCHAR(50) PRIMARY KEY,
    nombre               VARCHAR(255) NOT NULL,
    estado               BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   TIMESTAMP WITH TIME ZONE
);

-- Índices adicionales para mejorar rendimiento
CREATE INDEX idx_cuenta_numero_cuenta ON cuenta(numero_cuenta);
CREATE INDEX idx_cuenta_cliente_id ON cuenta(cliente_id);
CREATE INDEX idx_movimiento_cuenta_id ON movimiento(cuenta_id);
CREATE INDEX idx_movimiento_fecha ON movimiento(fecha);
