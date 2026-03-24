package com.junior.conta_transf;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.junior.conta_transf.DTO.ContaRequestDTO;
import com.junior.conta_transf.controller.ContaController;
import com.junior.conta_transf.enuns.ContaType;
import com.junior.conta_transf.exception.BusinessException;
import com.junior.conta_transf.exception.ClienteNaoEncontradoException;
import com.junior.conta_transf.exception.ExternalServiceUnavailableException;
import com.junior.conta_transf.exception.GlobalExceptionHandler;
import com.junior.conta_transf.service.ContaService;

@WebMvcTest(controllers = ContaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ContaControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContaService service;

    private static final ContaType TYPE = ContaType.values()[0];

    @Test
    void postContas_quandoBusinessException_deveRetornar422ApiError() throws Exception {
        when(service.create(any(ContaRequestDTO.class))).thenThrow(new BusinessException("Cliente inativo. id=99"));

        var body = """
            {"clienteId": 99, "type": "%s"}
            """.formatted(TYPE.name());

        mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is(422)))
                .andExpect(jsonPath("$.error", is("Regra de negócio")))
                .andExpect(jsonPath("$.message", is("Cliente inativo. id=99")))
                .andExpect(jsonPath("$.path", is("/contas")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getConta_quandoIllegalArgument_deveRetornar400ApiError() throws Exception {
        when(service.findById(10L)).thenThrow(new IllegalArgumentException("Conta não encontrada. id=10"));

        mockMvc.perform(get("/contas/10").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Requisição inválida")))
                .andExpect(jsonPath("$.message", is("Conta não encontrada. id=10")))
                .andExpect(jsonPath("$.path", is("/contas/10")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void postContas_quandoExternalServiceUnavailable_deveRetornar503ApiError() throws Exception {
        when(service.create(any(ContaRequestDTO.class)))
                .thenThrow(new ExternalServiceUnavailableException("cliente-service indisponível"));

        var body = """
            {"clienteId": 99, "type": "%s"}
            """.formatted(TYPE.name());

        mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status", is(503)))
                .andExpect(jsonPath("$.error", is("Serviço indisponível")))
                .andExpect(jsonPath("$.message", is("cliente-service indisponível")))
                .andExpect(jsonPath("$.path", is("/contas")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void postContas_quandoClienteNaoEncontrado_deveRetornar404ApiError() throws Exception {
        when(service.create(any(ContaRequestDTO.class)))
                .thenThrow(new ClienteNaoEncontradoException("Cliente não encontrado. id=99"));

        var body = """
            {"clienteId": 99, "type": "%s"}
            """.formatted(TYPE.name());

        mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Cliente não encontrado")))
                .andExpect(jsonPath("$.message", is("Cliente não encontrado. id=99")))
                .andExpect(jsonPath("$.path", is("/contas")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deleteContas_quandoDataIntegrityViolation_deveRetornar409ApiError() throws Exception {
        // Mockando direto no controller: simula o service deixando propagar.
        // (Mesmo que seu service real converta pra BusinessException, isso cobre o handler de 409.)
    	doThrow(new DataIntegrityViolationException("fk")).when(service).delete(10L);

        mockMvc.perform(delete("/contas/10").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Violação de integridade")))
                .andExpect(jsonPath("$.message", is("Operação não permitida (integridade)")))
                .andExpect(jsonPath("$.path", is("/contas/10")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void postContas_quandoJsonInvalido_deveRetornar400ApiError() throws Exception {
        mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("JSON inválido")))
                .andExpect(jsonPath("$.message", is("Body inválido ou malformado")))
                .andExpect(jsonPath("$.path", is("/contas")))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, never()).create(any());
    }
}