package com.junior.cliente.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.junior.cliente.entities.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	boolean existsByCpf(String cpf);
	boolean existsByEmail(String email);
	boolean existsByEmailAndIdNot(String email, Long id);
	boolean existsByCpfAndIdNot(String cpf, Long id);
	
	@Query("""
			select c
			from Cliente c
			where (:active is null or c.active = :active)
			""")
	Page<Cliente> findAllFiltered(@Param("active") Boolean active, Pageable pageable);
	


}
