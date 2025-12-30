package com.junior.conta_transf.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.junior.conta_transf.DTO.ClientValidationResponse;
import com.junior.conta_transf.config.FeignClientConfig;

@FeignClient(
		    name = "cliente-service",
		    url = "${cliente.service.url}",
		    configuration = FeignClientConfig.class
		)
		public interface IntegrationClient {

		    @GetMapping("/clientes/{id}")
		    ClientValidationResponse findById(@PathVariable Long id);
		}


