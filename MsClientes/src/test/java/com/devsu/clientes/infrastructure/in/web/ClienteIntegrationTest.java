package com.devsu.clientes.infrastructure.in.web;

import com.devsu.clientes.domain.port.out.ClienteRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Integración - Clientes API")
class ClienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepositoryPort clienteRepository;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("POST /clientes crea el cliente, responde 201 y lo persiste en la BD sin exponer la contraseña")
    void crearClienteIntegracion() throws Exception {
        String json = """
                {
                  "clienteId": "CLI001",
                  "nombre": "Jose Lema",
                  "genero": "M",
                  "edad": 30,
                  "identificacion": "1234567890",
                  "direccion": "Otavalo sn y principal",
                  "telefono": "098254785",
                  "contrasena": "1234",
                  "estado": true
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value("CLI001"))
                .andExpect(jsonPath("$.nombre").value("Jose Lema"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());

        // Verificación de integración: el cliente REALMENTE quedó en la base de datos
        assertThat(clienteRepository.existsByClienteId("CLI001")).isTrue();
    }
}