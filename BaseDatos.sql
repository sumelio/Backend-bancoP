-- ============================================================================
-- Database: clientesdb
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
    contrasena           VARCHAR(100) NOT NULL,
    estado               BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT fk_cliente_persona FOREIGN KEY (id) REFERENCES persona(id) 
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
-- Almacena la información de las cuentas bancarias de los clientes
CREATE TABLE cuenta (
    id                   BIGSERIAL PRIMARY KEY,
    numero_cuenta        VARCHAR(50) UNIQUE NOT NULL,
    tipo                 VARCHAR(50) NOT NULL CHECK (tipo IN ('AHORROS', 'CORRIENTE')),
    saldo_inicial        NUMERIC(15,2) NOT NULL DEFAULT 0,
    saldo_disponible     NUMERIC(15,2) NOT NULL DEFAULT 0,
    estado               BOOLEAN NOT NULL DEFAULT true,
    cliente_id           VARCHAR(50) NOT NULL,
    fecha_creacion       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   TIMESTAMP WITH TIME ZONE
);

-- Tabla: movimiento
-- Registra todos los movimientos (depósitos y retiros) de las cuentas
CREATE TABLE movimiento (
    id                   BIGSERIAL PRIMARY KEY,
    fecha                TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo                 VARCHAR(50) NOT NULL CHECK (tipo IN ('DEPOSITO', 'RETIRO')),
    valor                NUMERIC(15,2) NOT NULL,
    saldo                NUMERIC(15,2) NOT NULL,
    cuenta_id            BIGINT NOT NULL,
    fecha_creacion       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_movimiento_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuenta(id)
);

-- Tabla: cliente_local
-- Copia local de información del cliente sincronizada desde MS-Clientes vía RabbitMQ
CREATE TABLE cliente_local (
    cliente_id           VARCHAR(50) PRIMARY KEY,
    nombre               VARCHAR(255) NOT NULL,
    estado               BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   TIMESTAMP WITH TIME ZONE
);

-- ============================================================================
-- Índices para mejorar rendimiento
-- ============================================================================

-- Índices en cuenta
CREATE INDEX idx_cuenta_cliente_id ON cuenta(cliente_id);

-- Índices en movimiento
CREATE INDEX idx_movimiento_cuenta_id ON movimiento(cuenta_id);
CREATE INDEX idx_movimiento_fecha ON movimiento(fecha);

