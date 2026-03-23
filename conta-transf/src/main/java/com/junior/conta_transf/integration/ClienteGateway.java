package com.junior.conta_transf.integration;

import org.springframework.stereotype.Service;

import com.junior.conta_transf.DTO.ClientValidationResponse;

import io.github.resilience4j.retry.annotation.Retry;

@Service
public class ClienteGateway {

    private final IntegrationClient integrationClient;

    public ClienteGateway(IntegrationClient integrationClient) {
        this.integrationClient = integrationClient;
    }

    @Retry(name = "clienteRetry")
    public ClientValidationResponse buscarClientePorId(Long clienteId) {
        return integrationClient.findById(clienteId);
    }
}