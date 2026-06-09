# Backend Banco - Solución de Microservicios

Dos microservicios independientes: **MS-Clientes** (Persona, Cliente) 
y **MS-Cuentas** (Cuenta, Movimiento), comunicados de forma asíncrona mediante un tipico en RabbitMQ.

Repositorio: https://github.com/sumelio/Backend-bancoP

---

## Requisitos previos

- Docker Engine 20.10+
- Docker Compose v2
- Asignar al menos 4 GB de RAM a Docker 
- (se levantan 5 contenedores: 2 microservicios, 2 PostgreSQL y RabbitMQ).

---

## Despliegue

Desde la raíz del proyecto:

```bash
docker compose up --build
```

Esto construye las imágenes de los microservicios, levanta PostgreSQL y RabbitMQ, espera a que estén disponibles  
y arranca los servicios. Flyway aplica el esquema de base de datos automáticamente al iniciar.

### Puertos

| Servicio | URL |
|---|---|
| MS-Clientes | http://localhost:8090 |
| MS-Cuentas | http://localhost:8091 |
| RabbitMQ (UI de administración) | http://localhost:15672 |

Credenciales de RabbitMQ: usuario `devsu`, clave `devsu123`.

Conexion base de datos:

| Base de datos | URL | Username | Password |
|---|---|---|---|
| clientesdb | jdbc:postgresql://localhost:5434/clientesdb | devsu | devsu123 |
| cuentasdb | jdbc:postgresql://localhost:5433/cuentasdb | devsu | devsu123 |

### Detener

```bash
docker compose down        # detiene los contenedores
docker compose down -v     # además borra los volúmenes (datos)
```

### Refrescar cambios desde el código del microservicio
```bash
docker compose build --no-cache ms-cuentas                                                                     
docker compose up -d ms-cuentas
```

### Ver logs
```bash
docker compose logs -f
```
---

## Uso de la API

> **Importante:** crear primero los clientes. Al crearse un cliente, se publica un evento que MS-Cuentas consume para poblar su réplica local. La creación de cuentas valida que el cliente exista en esa réplica, por lo que debe esperarse un instante a que el evento se propague.

Orden recomendado:

1. **Crear cliente** -> `POST http://localhost:8090/clientes`
2. **Crear cuenta** -> `POST http://localhost:8091/cuentas`
3. **Registrar movimiento** -> `POST http://localhost:8091/movimientos`
4. **Generar reporte** -> `GET http://localhost:8091/reportes?clienteId=...&desde=...&hasta=...`

La colección de Postman con todos los casos de uso está en la carpeta `postman`.

### Endpoints

**MS-Clientes (`:8090`)**

| Verbo | Ruta | Descripción |
|---|---|---|
| POST | `/clientes` | Crear cliente |
| GET | `/clientes` | Listar clientes |
| GET | `/clientes/{id}` | Obtener cliente por id |
| PUT | `/clientes/{id}` | Actualizar cliente |
| DELETE | `/clientes/{id}` | Eliminar (borrado lógico) |

**MS-Cuentas (`:8091`)**

| Verbo | Ruta | Descripción |
|---|---|---|
| POST | `/cuentas` | Crear cuenta |
| GET | `/cuentas/{numeroCuenta}` | Obtene cuenta por número |
| GET | `/cuentas?clienteId=...` | Listar cuentas de un cliente |
| PUT | `/cuentas/{numeroCuenta}` | Actualiza cuenta |
| POST | `/movimientos` | Guarda movimiento |
| GET | `/reportes?clienteId=...&desde=...&hasta=...` | Reporte de estado de cuenta (rango de fechas en formato `yyyy-MM-dd`) |

---

