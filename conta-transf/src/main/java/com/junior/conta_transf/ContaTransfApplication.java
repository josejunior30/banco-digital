package com.junior.conta_transf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ContaTransfApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContaTransfApplication.class, args);
	}

}
