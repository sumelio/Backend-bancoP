Base de datos 1: clientesdb (MS-Clientes)
persona
─────────────────────────────
id              BIGINT  PK
nombre          VARCHAR
genero          VARCHAR
edad            INT
identificacion  VARCHAR UNIQUE   ← documento único
direccion       VARCHAR
telefono        VARCHAR
fecha_creacion      TIMESTAMP WITH TIME ZONE  NOT NULL
fecha_modificacion  TIMESTAMP WITH TIME ZONE

cliente
─────────────────────────────
id              BIGINT  PK, FK → persona(id)
cliente_id      VARCHAR UNIQUE   ← el "clienteid" del enunciado
contrasena      VARCHAR
estado          BOOLEAN
fecha_creacion      TIMESTAMP WITH TIME ZONE  NOT NULL
fecha_modificacion  TIMESTAMP WITH TIME ZONE



Base de datos 2: cuentasdb (MS-Cuentas)
cuenta
─────────────────────────────
id                BIGINT  PK
numero_cuenta     VARCHAR UNIQUE   ← clave única de negocio
tipo              VARCHAR          ← Ahorros / Corriente
saldo_inicial     NUMERIC(15,2)
saldo_disponible  NUMERIC(15,2)    ← se actualiza con cada movimiento
estado            BOOLEAN
cliente_id        VARCHAR          ← referencia lógica, NO FK real
fecha_creacion      TIMESTAMP WITH TIME ZONE  NOT NULL
fecha_modificacion  TIMESTAMP WITH TIME ZONE

movimiento
─────────────────────────────
id                BIGINT  PK
fecha             TIMESTAMP
tipo              VARCHAR          ← Deposito / Retiro
valor             NUMERIC(15,2)    ← + o –
saldo             NUMERIC(15,2)    ← saldo resultante tras el movimiento
cuenta_id         BIGINT  FK → cuenta(id)
fecha_creacion      TIMESTAMP WITH TIME ZONE  NOT NULL
fecha_modificacion  TIMESTAMP WITH TIME ZONE

cliente_local
─────────────────────────────
cliente_id        VARCHAR  PK      ← copia que llega por RabbitMQ
nombre            VARCHAR
estado            BOOLEAN
fecha_creacion      TIMESTAMP WITH TIME ZONE  NOT NULL
fecha_modificacion  TIMESTAMP WITH TIME ZONE