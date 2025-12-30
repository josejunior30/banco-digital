package com.junior.conta_transf.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.junior.conta_transf.exception.BusinessException;
import com.junior.conta_transf.integration.IntegrationClient;
import com.junior.conta_transf.repository.ContaRepository;
import com.junior.conta_transf.utilidades.GeradorNumeroContaUtils;

@Service
public class ContaService {

    private static final Logger log = LoggerFactory.getLogger(ContaService.class);

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
        if (id == null) throw new IllegalArgumentException("id é obrigatório");
        var entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada. id=" + id));
        return new ContaResponseDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id é obrigatório");
        if (!repository.existsById(id)) throw new IllegalArgumentException("Conta não encontrada. id=" + id);

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Não foi possível deletar a conta (integridade). id=" + id, e);
        }
    }

    @Transactional
    public Conta create(ContaRequestDTO requestDTO) {
        if (requestDTO == null) throw new IllegalArgumentException("Request inválida");
        if (requestDTO.clienteId() == null) throw new IllegalArgumentException("clienteId é obrigatório");
        if (requestDTO.type() == null) throw new IllegalArgumentException("type é obrigatório");

        log.info("create - validando clienteId={}", requestDTO.clienteId());

        ClientValidationResponse cliente;
        try {
            cliente = integrationClient.findById(requestDTO.clienteId());
        } catch (Exception e) {
            log.error("create - erro chamando cliente-service clienteId={}", requestDTO.clienteId(), e);
            throw new BusinessException("Falha na validação do cliente. id=" + requestDTO.clienteId(), e);
        }

        if (cliente == null) {
            log.warn("create - cliente-service retornou null para clienteId={}", requestDTO.clienteId());
            throw new BusinessException("Cliente não encontrado. id=" + requestDTO.clienteId());
        }

        log.info("create - cliente validado id={} active={}", cliente.id(), cliente.active());

        if (!cliente.active()) {
            throw new BusinessException("Cliente inativo. id=" + requestDTO.clienteId());
        }

        Conta conta = new Conta();
        conta.setClienteId(requestDTO.clienteId());
        conta.setType(requestDTO.type());
        conta.setBalance(BigDecimal.ZERO.setScale(2));
        conta.setStatus(ContaStatus.ATIVA);
        conta.setNumber(GeradorNumeroContaUtils.gerarNumeroConta("0001", repository::existsByNumber));

        Conta saved = repository.save(conta);

        log.info("create - conta criada id={} number={} clienteId={}",
                saved.getId(), saved.getNumber(), saved.getClienteId());

        return saved;
    }

    @Transactional
    public Conta patchTypeStatus(Long id, ContaPatchRequestDTO requestDTO) {
        if (id == null) throw new IllegalArgumentException("id é obrigatório");
        if (requestDTO == null) throw new IllegalArgumentException("Request inválida");
        if (requestDTO.type() == null && requestDTO.status() == null) {
            throw new IllegalArgumentException("Informe ao menos um campo: type ou status");
        }

        Conta conta = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada. id=" + id));

        if (requestDTO.type() != null) conta.setType(requestDTO.type());
        if (requestDTO.status() != null) conta.setStatus(requestDTO.status());

        Conta saved = repository.save(conta);

        log.info("patchTypeStatus - id={} type={} status={} version={}",
                saved.getId(), saved.getType(), saved.getStatus(), saved.getVersion());

        return saved;
    }
}