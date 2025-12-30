package com.junior.conta_transf.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junior.conta_transf.DTO.ContaPatchRequestDTO;
import com.junior.conta_transf.DTO.ContaRequestDTO;
import com.junior.conta_transf.DTO.ContaResponseDTO;
import com.junior.conta_transf.entities.Conta;
import com.junior.conta_transf.service.ContaService;

@RestController
@RequestMapping("/contas")
public class ContaController {
	
	private final ContaService service;

	public ContaController(ContaService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<Page<ContaResponseDTO>> findAll(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
		Page<ContaResponseDTO> page = service.findAll(pageable);
		return ResponseEntity.ok(page);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ContaResponseDTO> findById(@PathVariable Long id) {
		ContaResponseDTO dto = service.findById(id);
		return ResponseEntity.ok(dto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping
	public ResponseEntity<ContaResponseDTO> create(@RequestBody ContaRequestDTO request) {
		Conta conta = service.create(request);

		if (conta == null) {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(new ContaResponseDTO(conta));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ContaResponseDTO> patchTypeStatus(@PathVariable Long id,
			@RequestBody ContaPatchRequestDTO request) {
		Conta updated = service.patchTypeStatus(id, request);
		if (updated == null) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(new ContaResponseDTO(updated));
	}
}