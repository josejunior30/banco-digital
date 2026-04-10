package com.junior.notificacao.consumer;

import com.junior.notificacao.DTO.ClienteCriadoEvent;
import com.junior.notificacao.config.RabbitMQConfig;
import com.junior.notificacao.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ClienteCriadoConsumer {

    private static final Logger log = LoggerFactory.getLogger(ClienteCriadoConsumer.class);

    private final EmailService emailService;

    public ClienteCriadoConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumirClienteCriado(ClienteCriadoEvent event) {
        log.info("notificacao - evento recebido id={} email={}", event.id(), event.email());

        emailService.enviarBoasVindas(event);

        log.info("notificacao - processamento concluído para id={}", event.id());
    }
}