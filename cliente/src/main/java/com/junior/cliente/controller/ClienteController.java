package com.junior.cliente.controller;


import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.junior.cliente.DTO.ClienteRequestDTO;
import com.junior.cliente.DTO.ClienteResponseDTO;
import com.junior.cliente.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Operações de CRUD de clientes")
public class ClienteController {

	private final ClienteService service;

	public ClienteController(ClienteService service) {
		this.service = service;
	}
	@Operation(summary = "Criar cliente")
	@PostMapping
	public ResponseEntity<ClienteResponseDTO> create(@Valid @RequestBody ClienteRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar cliente por id")
	public ResponseEntity<ClienteResponseDTO> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@GetMapping
	@Operation(summary = "Listar clientes (paginado)")
	public ResponseEntity<Page<ClienteResponseDTO>> findAll(
	        @Parameter(description = "Filtra por ativo/inativo. Se omitido, retorna todos.")
	        @RequestParam(required = false) Boolean active,

	        @ParameterObject
	        @PageableDefault(size = 20, sort = "id")
	        Pageable pageable
	) {
	    return ResponseEntity.ok(service.findAll(active, pageable));
	}
	@PutMapping("/{id}")
	@Operation(summary = "Atualizar cliente")
	public ResponseEntity<ClienteResponseDTO> update(@PathVariable Long id,
			@Valid @RequestBody ClienteRequestDTO request) {
		return ResponseEntity.ok(service.update(id, request));
	}

	@PatchMapping("/{id}/deactivate")
	@Operation(summary = "Desativar cliente")
	public ResponseEntity<Void> deactivate(@PathVariable Long id) {
	    service.deactivate(id);
	    return ResponseEntity.noContent().build();
	}
	@DeleteMapping("/{id}")
	@Operation(summary = "Remover cliente")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
