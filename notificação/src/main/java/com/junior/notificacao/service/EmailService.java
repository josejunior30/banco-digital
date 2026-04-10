package com.junior.notificacao.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.junior.notificacao.DTO.ClienteCriadoEvent;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarBoasVindas(ClienteCriadoEvent event) {
        log.info("email - iniciando envio de e-mail para id={} email={}", event.id(), event.email());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(event.email());
        message.setSubject("Bem-vindo ao Banco Digital");
        message.setText("""
                Olá, %s!

                Seu cadastro foi criado com sucesso no Banco Digital.

                Dados recebidos:
                - Nome: %s
                - E-mail: %s
                - Status: %s

                Obrigado por se cadastrar.
                """.formatted(
                event.name(),
                event.name(),
                event.email(),
                event.active() ? "ATIVO" : "INATIVO"
        ));

        try {
            mailSender.send(message);
            log.info("email - e-mail enviado com sucesso para {}", event.email());
        } catch (MailException e) {
            log.error("email - erro ao enviar e-mail para {}", event.email(), e);
            throw e;
        }
    }
}