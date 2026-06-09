package com.devsu.clientes.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Clientes - Pruebas Unitaras de Entidad de Dominio")
class ClienteTest {

    @Test
    @DisplayName("Debe crear un cliente con constructor completo y verificar todos los campos")
    void debeCrearClienteConConstructorCompletoYVerificarTodosLosCampos() {
        // Dado, estos datos de entrada
        String nombre = "Jose Lema";
        String genero = "Masculino";
        Integer edad = 30;
        String identificacion = "1234567890";
        String direccion = "Otavalo sn y principal";
        String telefono = "098254785";
        String clienteId = "CLI001";
        String contrasena = "1234";
        Boolean estado = true;

        // Cuando: Llamamos al cliente
        Cliente cliente = new Cliente(
                nombre, genero, edad, identificacion,
                direccion, telefono, clienteId, contrasena, estado
        );

        // Entonces
        assertThat(cliente).isNotNull();

        // Verificar campos heredados de Persona
        assertThat(cliente.getNombre()).isEqualTo(nombre);
        assertThat(cliente.getGenero()).isEqualTo(genero);
        assertThat(cliente.getEdad()).isEqualTo(edad);
        assertThat(cliente.getIdentificacion()).isEqualTo(identificacion);
        assertThat(cliente.getDireccion()).isEqualTo(direccion);
        assertThat(cliente.getTelefono()).isEqualTo(telefono);

        // Verificar campos propios de Cliente
        assertThat(cliente.getClienteId()).isEqualTo(clienteId);
        assertThat(cliente.getContrasena()).isEqualTo(contrasena);
        assertThat(cliente.getEstado()).isEqualTo(estado);
    }
}