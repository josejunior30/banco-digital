package com.junior.cliente.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Banco Digital - Cliente API",
        version = "v1",
        description = "Cadastro e manutenção de clientes.",
        contact = @Contact(name = "Jose Junior", url = "https://github.com/josejunior30")
    )
)
public class OpenApiConfig {}