package com.junior.notificacao.consumer;

import com.junior.notificacao.DTO.ClienteCriadoEvent;
import com.junior.notificacao.config.RabbitMQConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ClienteCriadoConsumer {

    private static final Logger log = LoggerFactory.getLogger(ClienteCriadoConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumirClienteCriado(ClienteCriadoEvent event) {
        log.info("notificacao - evento recebido id={} email={} active={}",
                event.id(), event.email(), event.active());

        log.info("notificacao - processando notificação para cliente {}", event.name());

        
        log.info("notificacao - processamento concluído para id={}", event.id());
    }
}