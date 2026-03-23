package com.junior.conta_transf.integration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.junior.conta_transf.exception.ClienteNaoEncontradoException;
import com.junior.conta_transf.exception.ExternalServiceUnavailableException;

import feign.FeignException;


@Component
public class IntegrationClientFallbackFactory implements FallbackFactory<IntegrationClient> {

    private static final Logger log = LoggerFactory.getLogger(IntegrationClientFallbackFactory.class);

    @Override
    public IntegrationClient create(Throwable cause) {
        return id -> {
            log.error("Fallback acionado para integrationClient.findById({}). Motivo: {}", id, cause.toString());

            Throwable root = obterCausaRaiz(cause);

            if (root instanceof FeignException feignException) {
                int status = feignException.status();
                String content = feignException.contentUTF8();

                if (status == 404) {
                    throw new ClienteNaoEncontradoException("Cliente não encontrado. id=" + id);
                }

                if (status == 400 && content != null && content.contains("Cliente não encontrado")) {
                    throw new ClienteNaoEncontradoException("Cliente não encontrado. id=" + id);
                }

                if (status >= 500 || status == -1) {
                    throw new ExternalServiceUnavailableException(
                        "Serviço de cliente indisponível no momento. Tente novamente mais tarde.",
                        root
                    );
                }
            }

            if (ErroDeComunicacao(root)) {
                throw new ExternalServiceUnavailableException(
                    "Serviço de cliente indisponível no momento. Tente novamente mais tarde.",
                    root
                );
            }

            throw new ExternalServiceUnavailableException(
                "Falha ao consultar o serviço de cliente.",
                root
            );
        };
    }

    private Throwable obterCausaRaiz(Throwable erro) {
        Throwable causa = erro;

        while (causa.getCause() != null && causa != causa.getCause()) {
            causa = causa.getCause();
        }

        return causa;
    }

    private boolean ErroDeComunicacao(Throwable erro) {
        return erro instanceof java.net.ConnectException
                || erro instanceof java.net.SocketTimeoutException
                || erro instanceof java.net.UnknownHostException
                || erro instanceof feign.RetryableException;
    }

}