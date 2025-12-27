package com.junior.conta_transf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.junior.conta_transf.entities.Conta;

public interface ContaRepository extends JpaRepository<Conta, Long> {

}
