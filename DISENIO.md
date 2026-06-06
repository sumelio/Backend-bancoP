
Stack 
- Java 17
- Spring Boot 3.5.6
- RabbitMQ

Microservicio de Backend BP identificados.
1. MsClientes: Persona + Cliente.
2. MsCuentas: Cuenta + Movimiento.


Arquitectura limpia:
 Controller -> Service -> Repository -> Entity
    (API)       (logica)  (persistencia)  (dominio)
                  |                       |
                  |                       |
                  v                       v
             (DTOs Mapper)              (Entidades)
             

La comunicación debe ser asincrónica.
RabbitMQ es mas simple que Kaftka y suficiente para este caso usando docker.

Entendimiento del flujo

MsClientes --[ evento ClienteCreado/ClienteActualizado ]--> RabbitMQ --> MsCuentas (guardar datos localmente del cliente)


Definir entidades y tablas en base de datos.
Nombre de la base de datos del microservicio MsClientes: ms_clientes y su tablas son Persona y Cliente.
- Persona: 
    nombre
    genero
    edad
    identificacion
    direccion
    telefono
    La llave primaria es la identificacion
- Cliente: 
    id
    persona (relacion)
    contrasena
    estado
    La llave primaria es el id
    
Nombre de la base de datos del microservicio MsCuentas: ms_cuentas y su tablas son Cuenta y Movimiento.
- Cuenta:
    numeroCuenta
    tipoCuenta
    saldoInicial
    estado
    La llave primaria es el numero de cuenta
    cliente_id
    cliente_nombre
    
- Movimiento:
    fecha
    tipoMovimiento
    valor
    saldo
    cuenta (relacion)
    La llave primaria es la fecha (clave foranea a cuenta)
    
¿Como se releacion la cuenta con el cliente si estan en base de datos diferentes? 
Copia local de la tabla cliente en ms_cuentas.