package com.junior.cliente;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.junior.cliente.DTO.ClienteRequestDTO;
import com.junior.cliente.DTO.ClienteResponseDTO;
import com.junior.cliente.entities.Cliente;
import com.junior.cliente.exception.BusinessException;
import com.junior.cliente.exception.ResourceNotFoundException;
import com.junior.cliente.repository.ClienteRepository;
import com.junior.cliente.service.ClienteService;


@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Test
    void create_deveCriarCliente_quandoCpfEEmailNaoExistem() {
        ClienteRequestDTO request = new ClienteRequestDTO(
                "Ana",
                "12345678901",
                "ana@email.com",
                LocalDate.of(2000, 1, 1)
        );

        when(repository.existsByCpf(request.cpf())).thenReturn(false);
        when(repository.existsByEmail(request.email())).thenReturn(false);

        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            c.prePersist(); // simula @PrePersist do JPA
            c.setId(1L);
            return c;
        });

        ClienteResponseDTO response = service.create(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Ana");
        assertThat(response.cpf()).isEqualTo("12345678901");
        assertThat(response.email()).isEqualTo("ana@email.com");
        assertThat(response.active()).isTrue();

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(repository).save(captor.capture());
        Cliente savedArg = captor.getValue();

        assertThat(savedArg.getName()).isEqualTo("Ana");
        assertThat(savedArg.getCpf()).isEqualTo("12345678901");
        assertThat(savedArg.getEmail()).isEqualTo("ana@email.com");
        assertThat(savedArg.getBirthDate()).isEqualTo(LocalDate.of(2000, 1, 1));
    }

    @Test
    void create_deveLancarBusinessException_quandoCpfJaExiste() {
        ClienteRequestDTO request = new ClienteRequestDTO(
                "Ana",
                "12345678901",
                "ana@email.com",
                LocalDate.of(2000, 1, 1)
        );

        when(repository.existsByCpf(request.cpf())).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("CPF já cadastrado");

        verify(repository, never()).save(any());
    }

    @Test
    void create_deveLancarBusinessException_quandoEmailJaExiste() {
        ClienteRequestDTO request = new ClienteRequestDTO(
                "Ana",
                "12345678901",
                "ana@email.com",
                LocalDate.of(2000, 1, 1)
        );

        when(repository.existsByCpf(request.cpf())).thenReturn(false);
        when(repository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email já cadastrado");

        verify(repository, never()).save(any());
    }

    @Test
    void findById_deveRetornarCliente_quandoExiste() {
        Cliente c = new Cliente(10L, "Bob", "11122233344", "bob@email.com", LocalDate.of(1990, 5, 5), true);
        when(repository.findById(10L)).thenReturn(Optional.of(c));

        ClienteResponseDTO response = service.findById(10L);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Bob");
        assertThat(response.cpf()).isEqualTo("11122233344");
        assertThat(response.email()).isEqualTo("bob@email.com");
        assertThat(response.active()).isTrue();
    }

    @Test
    void findById_deveLancarResourceNotFoundException_quandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cliente não encontrado com id: 99");
    }

    @Test
    void findAll_deveRetornarPageDeClientes_quandoSemFiltroActive() {
        Cliente c1 = new Cliente(1L, "A", "111", "a@email.com", LocalDate.of(2000, 1, 1), true);
        Cliente c2 = new Cliente(2L, "B", "222", "b@email.com", LocalDate.of(2001, 2, 2), false);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> page = new PageImpl<>(List.of(c1, c2), pageable, 2);

        when(repository.findAllFiltered(null, pageable)).thenReturn(page);

        Page<ClienteResponseDTO> response = service.findAll(null, pageable);

        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).id()).isEqualTo(1L);
        assertThat(response.getContent().get(0).active()).isTrue();
        assertThat(response.getContent().get(1).id()).isEqualTo(2L);
        assertThat(response.getContent().get(1).active()).isFalse();

        verify(repository).findAllFiltered(null, pageable);
    }

    @Test
    void findAll_deveFiltrarPorActiveTrue_quandoInformado() {
        Cliente c1 = new Cliente(1L, "A", "111", "a@email.com", LocalDate.of(2000, 1, 1), true);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> page = new PageImpl<>(List.of(c1), pageable, 1);

        when(repository.findAllFiltered(true, pageable)).thenReturn(page);

        Page<ClienteResponseDTO> response = service.findAll(true, pageable);

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).active()).isTrue();

        verify(repository).findAllFiltered(true, pageable);
    }

    @Test
    void update_deveAtualizarCampos_quandoExiste_eNaoHaDuplicidade() {
        Cliente existing = new Cliente(5L, "Antigo", "99988877766", "old@email.com", LocalDate.of(1999, 9, 9), true);
        ClienteRequestDTO request = new ClienteRequestDTO(
                "Novo Nome",
                "99988877766",
                "novo@email.com",
                LocalDate.of(1999, 9, 9)
        );

        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.existsByEmailAndIdNot(request.email(), 5L)).thenReturn(false);
        when(repository.existsByCpfAndIdNot(request.cpf(), 5L)).thenReturn(false);
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClienteResponseDTO response = service.update(5L, request);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.name()).isEqualTo("Novo Nome");
        assertThat(response.email()).isEqualTo("novo@email.com");
        assertThat(response.cpf()).isEqualTo("99988877766");
        assertThat(response.active()).isTrue();

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(repository).save(captor.capture());
        Cliente savedArg = captor.getValue();

        assertThat(savedArg.getName()).isEqualTo("Novo Nome");
        assertThat(savedArg.getEmail()).isEqualTo("novo@email.com");
        assertThat(savedArg.getCpf()).isEqualTo("99988877766");
        assertThat(savedArg.getBirthDate()).isEqualTo(LocalDate.of(1999, 9, 9));
    }

    @Test
    void update_deveLancarResourceNotFoundException_quandoNaoExiste() {
        ClienteRequestDTO request = new ClienteRequestDTO(
                "X",
                "12345678901",
                "x@email.com",
                LocalDate.of(2000, 1, 1)
        );

        when(repository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(123L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cliente não encontrado com id: 123");
    }

    @Test
    void update_deveLancarBusinessException_quandoEmailJaExisteEmOutroCliente() {
        Cliente existing = new Cliente(5L, "Antigo", "99988877766", "old@email.com", LocalDate.of(1999, 9, 9), true);
        ClienteRequestDTO request = new ClienteRequestDTO(
                "Novo Nome",
                "99988877766",
                "novo@email.com",
                LocalDate.of(1999, 9, 9)
        );

        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.existsByEmailAndIdNot(request.email(), 5L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(5L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email já cadastrado para outro cliente");

        verify(repository, never()).save(any());
    }

    @Test
    void update_deveLancarBusinessException_quandoCpfJaExisteEmOutroCliente() {
        Cliente existing = new Cliente(5L, "Antigo", "99988877766", "old@email.com", LocalDate.of(1999, 9, 9), true);
        ClienteRequestDTO request = new ClienteRequestDTO(
                "Novo Nome",
                "99988877766",
                "novo@email.com",
                LocalDate.of(1999, 9, 9)
        );

        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.existsByEmailAndIdNot(request.email(), 5L)).thenReturn(false);
        when(repository.existsByCpfAndIdNot(request.cpf(), 5L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(5L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("CPF já cadastrado para outro cliente");

        verify(repository, never()).save(any());
    }

    @Test
    void delete_deveChamarDeleteById_quandoExiste() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_deveLancarResourceNotFoundException_quandoNaoExiste() {
        when(repository.existsById(404L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cliente não encontrado com id: 404");

        verify(repository, never()).deleteById(anyLong());
    }
}
