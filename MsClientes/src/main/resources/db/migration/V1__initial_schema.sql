-- ============================================================================
-- Database: clientesdb
-- ============================================================================

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