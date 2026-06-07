package com.devsu.cuentas.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el microservicio MsCuentas.
 *
 * Define la cola para escuchar eventos de clientes desde MS-Clientes
 * y configura la serialización/deserialización JSON para mensajes.
 */
@Configuration
public class RabbitConfig {

    public static final String CLIENTE_EXCHANGE = "cliente.exchange";
    public static final String CLIENTE_QUEUE = "cliente.cuentas.queue";
    public static final String ROUTING_KEY = "cliente.*";

    /**
     * Define la cola durable para recibir eventos de clientes.
     * La cola persiste los mensajes en disco para evitar pérdida de datos.
     *
     * @return Queue durable para eventos de clientes
     */
    @Bean
    public Queue clienteQueue() {
        //el buzón donde se acumulan los mensajes hasta que tu listener los procesa. Es durable (sobrevive a reinicios del broker)
        return new Queue(CLIENTE_QUEUE, true);  // true = durable
    }

    /**
     * Define el exchange tipo Topic al que se publica desde MS-Clientes.
     *
     * @return TopicExchange para eventos de clientes
     */
    @Bean
    public TopicExchange clienteExchange() {
        return new TopicExchange(CLIENTE_EXCHANGE);
    }

    /**
     * Vincula la cola con el exchange usando una routing key.
     * El patrón "cliente.*" captura todos los eventos relacionados con clientes (crear, actualizar).
     *
     * @param clienteQueue cola de destino
     * @param clienteExchange exchange de origen
     * @return Binding entre queue y exchange
     */
    @Bean
    public Binding binding(Queue clienteQueue, TopicExchange clienteExchange) {
        return BindingBuilder
                .bind(clienteQueue)
                .to(clienteExchange)
                .with(ROUTING_KEY);
    }

    /**
     * Configura el convertidor de mensajes para serializar/deserializar JSON.
     *
     * @param objectMapper mapper de Jackson para conversión JSON
     * @return Jackson2JsonMessageConverter configurado
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);   // ← esto es lo que hace que el @RabbitListener deserialice JSON
        return factory;
    }
}