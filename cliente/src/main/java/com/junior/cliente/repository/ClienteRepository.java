package com.junior.cliente.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.junior.cliente.entities.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	boolean existsByCpf(String cpf);
}
