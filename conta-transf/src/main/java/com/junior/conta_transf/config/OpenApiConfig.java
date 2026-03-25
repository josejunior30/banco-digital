package com.junior.conta_transf.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Banco Digital - Conta/Transferência API",
        version = "v1",
        description = "Abertura e manutenção de contas (valida cliente via cliente-service).",
        contact = @Contact(name = "Jose Junior", url = "https://github.com/josejunior30")
    )
)
public class OpenApiConfig {}