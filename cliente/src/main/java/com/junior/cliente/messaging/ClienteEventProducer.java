package com.junior.cliente.messaging;


import com.junior.cliente.DTO.ClienteCriadoEvent;
import com.junior.cliente.config.RabbitMQConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClienteEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ClienteEventProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public ClienteEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarClienteCriado(ClienteCriadoEvent event) {
        log.info("rabbit - publicando evento cliente.criado id={} email={}",
                event.id(), event.email());

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    event
            );

            log.info("rabbit - evento publicado com sucesso exchange={} routingKey={} id={}",
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    event.id());

        } catch (AmqpException e) {
            log.error("rabbit - erro ao publicar evento cliente.criado id={} email={}",
                    event.id(), event.email(), e);
            throw e;
        }
    }
}