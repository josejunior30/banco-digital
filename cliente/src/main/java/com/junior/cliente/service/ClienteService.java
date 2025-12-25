package com.junior.cliente.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.junior.cliente.DTO.ClienteRequestDTO;
import com.junior.cliente.DTO.ClienteResponseDTO;
import com.junior.cliente.entities.Cliente;
import com.junior.cliente.exception.BusinessException;
import com.junior.cliente.repository.ClienteRepository;

@Service
public class ClienteService {

	private final ClienteRepository repository;

	public ClienteService(ClienteRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public ClienteResponseDTO create(ClienteRequestDTO request) {

		if (repository.existsByCpf(request.cpf())) {
		    throw new BusinessException("CPF já cadastrado");
		}

		
		Cliente cliente = new Cliente();
		cliente.setName(request.name());
		cliente.setCpf(request.cpf());
		cliente.setEmail(request.email());
		cliente.setBirthDate(request.birthDate());
		Cliente saved = repository.save(cliente);

		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public ClienteResponseDTO findById(Long id) {
		Cliente customer = repository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com id: " + id));

		return toResponse(customer);
	}

	@Transactional(readOnly = true)
	public List<ClienteResponseDTO> findAll() {
		return repository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional
	public ClienteResponseDTO update(Long id, ClienteRequestDTO request) {

		Cliente cliente = repository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com id: " + id));

		cliente.setName(request.name());
		cliente.setEmail(request.email());

		Cliente updated = repository.save(cliente);

		return toResponse(updated);
	}

	public void delete(Long id) {

		if (!repository.existsById(id)) {

		}

		repository.deleteById(id);
	}

	private ClienteResponseDTO toResponse(Cliente customer) {
		return new ClienteResponseDTO(customer.getId(), customer.getName(), customer.getCpf(), customer.getEmail(),
				customer.isActive());
	}
}
