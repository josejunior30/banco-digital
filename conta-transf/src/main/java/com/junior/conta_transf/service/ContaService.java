package com.junior.conta_transf.service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.junior.conta_transf.DTO.ClientValidationResponse;
import com.junior.conta_transf.DTO.ContaPatchRequestDTO;
import com.junior.conta_transf.DTO.ContaRequestDTO;
import com.junior.conta_transf.DTO.ContaResponseDTO;
import com.junior.conta_transf.entities.Conta;
import com.junior.conta_transf.enuns.ContaStatus;
import com.junior.conta_transf.integration.IntegrationClient;
import com.junior.conta_transf.repository.ContaRepository;

@Service
public class ContaService {

	private final ContaRepository repository;
	private final IntegrationClient integrationClient;

	
	public ContaService(ContaRepository repository, IntegrationClient integrationClient) {
		this.repository = repository;
		this.integrationClient = integrationClient;
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
	
	 @Transactional
	    public Conta create(ContaRequestDTO requestDTO) {
	        if (requestDTO == null) {
	            throw new IllegalArgumentException("Request inválida");
	        }
	        if (requestDTO.clienteId() == null) {
	            throw new IllegalArgumentException("clienteId é obrigatório");
	        }
	        if (requestDTO.type() == null) {
	            throw new IllegalArgumentException("type é obrigatório");
	        }

	  
	        ClientValidationResponse cliente = integrationClient.findById(requestDTO.clienteId());
	        System.out.println("Cliente recebido: " + cliente);

	        if (cliente == null || !cliente.active()) {
	            throw new IllegalStateException("Cliente inativo. id=" + requestDTO.clienteId());
	        }

	        Conta conta = new Conta();
	        conta.setClienteId(requestDTO.clienteId());
	        conta.setType(requestDTO.type());
	        conta.setBalance(BigDecimal.ZERO.setScale(2));
	        conta.setStatus(ContaStatus.ATIVA);
	        conta.setNumber(gerarNumeroConta("0001"));
	        return repository.save(conta);
	    }

	 @Transactional
	    public Conta patchTypeStatus(Long id, ContaPatchRequestDTO requestDTO) {
	        if (id == null || requestDTO == null) {
	            return null;
	        }

	        Conta conta = repository.findById(id).orElse(null);
	        if (conta == null) {
	            return null;
	        }

	        if (requestDTO.type() != null) {
	            conta.setType(requestDTO.type());
	        }
	        if (requestDTO.status() != null) {
	            conta.setStatus(requestDTO.status());
	        }

	        return repository.save(conta);
	    }
	 private String gerarNumeroConta(String agency4) {
	        String agency = somenteDigitos(agency4);
	        if (agency.length() != 4) {
	            agency = String.format("%04d", parseIntSeguro(agency, 1));
	        }

	        for (int attempt = 0; attempt < 20; attempt++) {
	            int raw = ThreadLocalRandom.current().nextInt(0, 100_000_000);
	            String account8 = String.format("%08d", raw);

	            String digits = agency + account8;
	            int dv = calcularDigitoVerificadorLuhn(digits);

	            String formatted = agency + "-" + account8 + "-" + dv;

	            if (!repository.existsByNumber(formatted)) {
	                return formatted;
	            }
	        }

	        throw new IllegalStateException("Falha ao gerar número de conta único");
	    }

	    private static String somenteDigitos(String s) {
	        if (s == null) return "";
	        return s.replaceAll("\\D+", "");
	    }

	    private static int parseIntSeguro(String s, int fallback) {
	        try {
	            return Integer.parseInt(s);
	        } catch (Exception ignored) {
	            return fallback;
	        }
	    }

	    // Luhn: retorna o dígito verificador (0-9)
	    private static int calcularDigitoVerificadorLuhn(String digits) {
	        int sum = 0;
	        boolean doubleIt = true;
	        for (int i = digits.length() - 1; i >= 0; i--) {
	            int d = digits.charAt(i) - '0';
	            int add = d;
	            if (doubleIt) {
	                add = d * 2;
	                if (add > 9) add -= 9;
	            }
	            sum += add;
	            doubleIt = !doubleIt;
	        }
	        return (10 - (sum % 10)) % 10;
	    }

	    public void validateActive(Conta conta) {
	        if (conta == null) {
	            throw new IllegalArgumentException("Conta nula");
	        }
	        if (conta.getStatus() != ContaStatus.ATIVA) {
	            throw new IllegalStateException(
	                    "Conta não está ATIVA. id=" + conta.getId() + " status=" + conta.getStatus()
	            );
	        }
	    }

	  
}
    