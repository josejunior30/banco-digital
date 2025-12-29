package com.junior.conta_transf.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.junior.conta_transf.DTO.ContaResponseDTO;
import com.junior.conta_transf.repository.ContaRepository;
@Service
public class ContaService {

	private final ContaRepository repository;

	public ContaService(ContaRepository repository) {
		this.repository = repository;
	}
	
	 @Transactional(readOnly = true)
	    public Page<ContaResponseDTO> findAll(Pageable pageable) {
	        return repository.findAll(pageable).map(ContaResponseDTO::new);
	    }

	  @Transactional(readOnly = true)
	    public ContaResponseDTO findById(Long id) {
	        var entity = repository.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada. id=" + id));
	        return new ContaResponseDTO(entity);
	    }
	  @Transactional
	    public void delete(Long id) {
	        if (!repository.existsById(id)) {
	            throw new IllegalArgumentException("Conta não encontrada. id=" + id);
	        }
	        try {
	            repository.deleteById(id);
	        } catch (DataIntegrityViolationException e) {
	            throw new IllegalArgumentException("Não foi possível deletar a conta (integridade). id=" + id, e);
	        }
	    }
}
