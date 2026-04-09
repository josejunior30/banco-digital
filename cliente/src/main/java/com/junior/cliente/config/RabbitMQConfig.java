package com.junior.cliente.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "cliente.exchange";
    public static final String QUEUE = "cliente.notificacao.queue";
    public static final String ROUTING_KEY = "cliente.criado";

    @Bean
    DirectExchange clienteExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    Queue clienteNotificacaoQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    Binding clienteBinding(Queue clienteNotificacaoQueue, DirectExchange clienteExchange) {
        return BindingBuilder
                .bind(clienteNotificacaoQueue)
                .to(clienteExchange)
                .with(ROUTING_KEY);
    }
}