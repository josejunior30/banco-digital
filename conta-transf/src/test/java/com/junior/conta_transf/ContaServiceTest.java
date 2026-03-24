package com.junior.conta_transf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.junior.conta_transf.DTO.ClientValidationResponse;
import com.junior.conta_transf.DTO.ContaPatchRequestDTO;
import com.junior.conta_transf.DTO.ContaRequestDTO;
import com.junior.conta_transf.DTO.ContaResponseDTO;
import com.junior.conta_transf.entities.Conta;
import com.junior.conta_transf.enuns.ContaStatus;
import com.junior.conta_transf.enuns.ContaType;
import com.junior.conta_transf.exception.BusinessException;
import com.junior.conta_transf.exception.ClienteNaoEncontradoException;
import com.junior.conta_transf.exception.ExternalServiceUnavailableException;
import com.junior.conta_transf.integration.ClienteGateway;
import com.junior.conta_transf.repository.ContaRepository;
import com.junior.conta_transf.service.ContaService;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository repository;

    @Mock
    private ClienteGateway clienteGateway;

    private ContaService service;

    private static final ContaType TYPE = ContaType.values()[0];
    private static final ContaStatus STATUS = ContaStatus.values()[0];

    @BeforeEach
    void setUp() {
        this.service = new ContaService(repository, clienteGateway);
    }

    @Test
    void findAll_deveMapearParaDTO() {
        var pageable = PageRequest.of(0, 20);

        var c1 = conta(1L, "0001-00000001", TYPE, bd("10.00"), STATUS, 11L);
        var c2 = conta(2L, "0001-00000002", TYPE, bd("20.00"), STATUS, 22L);

        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(java.util.List.of(c1, c2), pageable, 2));

        Page<ContaResponseDTO> page = service.findAll(pageable);

        assertEquals(2, page.getContent().size());
        assertEquals(1L, page.getContent().get(0).getId());
        assertEquals("0001-00000001", page.getContent().get(0).getNumber());
        verify(repository).findAll(pageable);
    }

    @Test
    void findById_quandoIdNull_deveLancar() {
        assertThrows(IllegalArgumentException.class, () -> service.findById(null));
    }

    @Test
    void findById_quandoExiste_deveRetornarDTO() {
        var c = conta(10L, "0001-00000010", TYPE, bd("5.50"), STATUS, 99L);
        when(repository.findById(10L)).thenReturn(Optional.of(c));

        ContaResponseDTO dto = service.findById(10L);

        assertEquals(10L, dto.getId());
        assertEquals("0001-00000010", dto.getNumber());
        verify(repository).findById(10L);
    }

    @Test
    void findById_quandoNaoExiste_deveLancar() {
        when(repository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.findById(10L));
    }

    @Test
    void delete_quandoIdNull_deveLancar() {
        assertThrows(IllegalArgumentException.class, () -> service.delete(null));
    }

    @Test
    void delete_quandoNaoExiste_deveLancar() {
        when(repository.existsById(10L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.delete(10L));
        verify(repository).existsById(10L);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void delete_quandoIntegrityViolation_deveLancarBusinessException() {
        when(repository.existsById(10L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("fk")).when(repository).deleteById(10L);

        assertThrows(BusinessException.class, () -> service.delete(10L));
        verify(repository).deleteById(10L);
    }

    @Test
    void delete_quandoOk_deveDeletar() {
        when(repository.existsById(10L)).thenReturn(true);

        service.delete(10L);

        verify(repository).deleteById(10L);
    }

    @Test
    void create_quandoRequestNull_deveLancar() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
    }

    @Test
    void create_quandoClienteIdNull_deveLancar() {
        assertThrows(IllegalArgumentException.class, () -> service.create(new ContaRequestDTO(null, TYPE)));
    }

    @Test
    void create_quandoTypeNull_deveLancar() {
        assertThrows(IllegalArgumentException.class, () -> service.create(new ContaRequestDTO(1L, null)));
    }

    @Test
    void create_quandoClienteInativo_deveLancarBusinessException() {
        when(clienteGateway.buscarClientePorId(1L)).thenReturn(new ClientValidationResponse(1L, false));

        assertThrows(BusinessException.class, () -> service.create(new ContaRequestDTO(1L, TYPE)));
        verify(repository, never()).save(any());
    }

    @Test
    void create_quandoGatewayClienteNaoEncontrado_devePropagar() {
        when(clienteGateway.buscarClientePorId(1L)).thenThrow(new ClienteNaoEncontradoException("x"));

        assertThrows(ClienteNaoEncontradoException.class, () -> service.create(new ContaRequestDTO(1L, TYPE)));
        verify(repository, never()).save(any());
    }

    @Test
    void create_quandoGatewayIndisponivel_devePropagar() {
        when(clienteGateway.buscarClientePorId(1L)).thenThrow(new ExternalServiceUnavailableException("down"));

        assertThrows(ExternalServiceUnavailableException.class, () -> service.create(new ContaRequestDTO(1L, TYPE)));
        verify(repository, never()).save(any());
    }

    @Test
    void create_quandoOk_deveSalvarContaComDefaults() {
        when(clienteGateway.buscarClientePorId(1L)).thenReturn(new ClientValidationResponse(1L, true));
        when(repository.existsByNumber(anyString())).thenReturn(false);
        when(repository.save(any(Conta.class))).thenAnswer(inv -> inv.getArgument(0));

        Conta saved = service.create(new ContaRequestDTO(1L, TYPE));

        assertNotNull(saved);
        assertEquals(1L, saved.getClienteId());
        assertEquals(TYPE, saved.getType());
        assertEquals(bd("0.00"), saved.getBalance());
        assertEquals(ContaStatus.ATIVA, saved.getStatus());
        assertNotNull(saved.getNumber());

        ArgumentCaptor<Conta> captor = ArgumentCaptor.forClass(Conta.class);
        verify(repository).save(captor.capture());
        assertEquals(1L, captor.getValue().getClienteId());
    }

    @Test
    void patchTypeStatus_quandoIdNull_deveLancar() {
        assertThrows(IllegalArgumentException.class, () -> service.patchTypeStatus(null, new ContaPatchRequestDTO(null, TYPE)));
    }

    @Test
    void patchTypeStatus_quandoRequestNull_deveLancar() {
        assertThrows(IllegalArgumentException.class, () -> service.patchTypeStatus(1L, null));
    }

    @Test
    void patchTypeStatus_quandoNaoInformarCampos_deveLancar() {
        assertThrows(IllegalArgumentException.class, () -> service.patchTypeStatus(1L, new ContaPatchRequestDTO(null, null)));
    }

    @Test
    void patchTypeStatus_quandoContaNaoExiste_deveLancar() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.patchTypeStatus(1L, new ContaPatchRequestDTO(STATUS, TYPE)));
    }

    @Test
    void patchTypeStatus_quandoOk_deveAtualizarCamposInformados() {
        var original = conta(1L, "0001-00000001", TYPE, bd("0.00"), STATUS, 1L);
        var newType = TYPE;
        var newStatus = STATUS;

        when(repository.findById(1L)).thenReturn(Optional.of(original));
        when(repository.save(any(Conta.class))).thenAnswer(inv -> inv.getArgument(0));

        Conta updated = service.patchTypeStatus(1L, new ContaPatchRequestDTO(newStatus, newType));

        assertEquals(newType, updated.getType());
        assertEquals(newStatus, updated.getStatus());
        verify(repository).save(original);
    }

    private static BigDecimal bd(String v) {
        return new BigDecimal(v).setScale(2);
    }

    private static Conta conta(Long id, String number, ContaType type, BigDecimal balance, ContaStatus status, Long clienteId) {
        var c = new Conta();
        TestReflection.setField(c, "id", id);
        c.setNumber(number);
        c.setType(type);
        c.setBalance(balance);
        c.setStatus(status);
        c.setClienteId(clienteId);
        return c;
    }

    static final class TestReflection {
        static void setField(Object target, String fieldName, Object value) {
            try {
                Field f = target.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                f.set(target, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private TestReflection() {}
    }
    
}