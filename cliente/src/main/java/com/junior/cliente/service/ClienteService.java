package com.junior.cliente.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.junior.cliente.DTO.ClienteRequestDTO;
import com.junior.cliente.DTO.ClienteResponseDTO;
import com.junior.cliente.entities.Cliente;
import com.junior.cliente.exception.BusinessException;
import com.junior.cliente.exception.ResourceNotFoundException;
import com.junior.cliente.repository.ClienteRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ClienteService {

	private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

	private final ClienteRepository repository;

	public ClienteService(ClienteRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public ClienteResponseDTO create(ClienteRequestDTO request) {
		log.info("create - iniciando cadastro de cliente cpf={} email={}", request.cpf(), request.email());

		if (repository.existsByCpf(request.cpf())) {
			log.warn("create - cpf já cadastrado cpf={}", request.cpf());
			throw new BusinessException("CPF já cadastrado");
		}

		if (repository.existsByEmail(request.email())) {
			log.warn("create - email já cadastrado email={}", request.email());
		    throw new BusinessException("Email já cadastrado");
		}

		Cliente cliente = new Cliente();
		cliente.setName(request.name());
		cliente.setCpf(request.cpf());
		cliente.setEmail(request.email());
		cliente.setBirthDate(request.birthDate());

		Cliente saved = repository.save(cliente);

		log.info("create - cliente criado com sucesso id={} cpf={} email={}",
				saved.getId(), saved.getCpf(), saved.getEmail());

		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public ClienteResponseDTO findById(Long id) {
		Cliente customer = repository.findById(id)
				.orElseThrow(() -> {
					log.warn("findById - cliente não encontrado id={}", id);
					return new ResourceNotFoundException("Cliente não encontrado com id: " + id);
				});

		log.info("findById - cliente encontrado id={} active={}", customer.getId(), customer.isActive());

		return toResponse(customer);
	}

	@Transactional(readOnly = true)
	public Page<ClienteResponseDTO> findAll(Boolean active, Pageable pageable) {
		log.info("findAll - listando clientes active={} page={} size={}",
				active, pageable.getPageNumber(), pageable.getPageSize());

		Page<ClienteResponseDTO> result = repository.findAllFiltered(active, pageable).map(this::toResponse);

		log.info("findAll - totalElements={} totalPages={}",
				result.getTotalElements(), result.getTotalPages());

		return result;
	}

	@Transactional
	public ClienteResponseDTO update(Long id, ClienteRequestDTO request) {
		log.info("update - iniciando atualização id={} cpf={} email={}", id, request.cpf(), request.email());

		Cliente cliente = repository.findById(id)
				.orElseThrow(() -> {
					log.warn("update - cliente não encontrado id={}", id);
					return new ResourceNotFoundException("Cliente não encontrado com id: " + id);
				});

		if (repository.existsByEmailAndIdNot(request.email(), id)) {
			log.warn("update - email já cadastrado para outro cliente id={} email={}", id, request.email());
			throw new BusinessException("Email já cadastrado para outro cliente");
		}

		if (repository.existsByCpfAndIdNot(request.cpf(), id)) {
			log.warn("update - cpf já cadastrado para outro cliente id={} cpf={}", id, request.cpf());
			throw new BusinessException("CPF já cadastrado para outro cliente");
		}

		cliente.setName(request.name());
		cliente.setCpf(request.cpf());
		cliente.setEmail(request.email());
		cliente.setBirthDate(request.birthDate());

		Cliente updated = repository.save(cliente);

		log.info("update - cliente atualizado com sucesso id={} cpf={} email={}",
				updated.getId(), updated.getCpf(), updated.getEmail());

		return toResponse(updated);
	}
	@Transactional
	public void deactivate(Long id) {
	    Cliente cliente = repository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + id));

	    if (!cliente.isActive()) {
	        throw new BusinessException("Cliente já está inativo");
	    }

	    cliente.setActive(false);
	    repository.save(cliente);
	}
	@Transactional
	public void delete(Long id) {
	
		if (!repository.existsById(id)) {
			log.warn("delete - cliente não encontrado id={}", id);
			throw new ResourceNotFoundException("Cliente não encontrado com id: " + id);
		}

		repository.deleteById(id);

		log.info("delete - cliente removido com sucesso id={}", id);
	}

	private ClienteResponseDTO toResponse(Cliente customer) {
		return new ClienteResponseDTO(
				customer.getId(),
				customer.getName(),
				customer.getCpf(),
				customer.getEmail(),
				customer.isActive()
		);
	}
}