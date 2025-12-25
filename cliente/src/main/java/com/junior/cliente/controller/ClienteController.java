package com.junior.cliente.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junior.cliente.DTO.ClienteRequestDTO;
import com.junior.cliente.DTO.ClienteResponseDTO;
import com.junior.cliente.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

	private final ClienteService service;

	public ClienteController(ClienteService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<ClienteResponseDTO> create(@Validated @RequestBody ClienteRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClienteResponseDTO> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@GetMapping
	public ResponseEntity<List<ClienteResponseDTO>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@PutMapping("/{id}")
	public ResponseEntity<ClienteResponseDTO> update(@Validated @PathVariable Long id,
			@RequestBody ClienteRequestDTO request) {
		return ResponseEntity.ok(service.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
