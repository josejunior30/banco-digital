package com.junior.cliente;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Method;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.junior.cliente.DTO.ApiError;
import com.junior.cliente.DTO.ClienteRequestDTO;
import com.junior.cliente.exception.BusinessException;
import com.junior.cliente.exception.GlobalExceptionHandler;
import com.junior.cliente.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBusiness_deveRetornar400_comMensagem() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURI()).thenReturn("/clientes");

        ResponseEntity<ApiError> response =
                handler.handleBusiness(new BusinessException("CPF já cadastrado"), req);

        assertThat(response.getStatusCode().value()).isEqualTo(400);

        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.error()).isEqualTo("Bad Request");
        assertThat(body.message()).isEqualTo("CPF já cadastrado");
        assertThat(body.path()).isEqualTo("/clientes");
        assertThat(body.timestamp()).isNotNull();
    }

    @Test
    void handleDataIntegrity_deveRetornar409() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURI()).thenReturn("/clientes");

        ResponseEntity<ApiError> response =
                handler.handleDataIntegrity(new DataIntegrityViolationException("x"), req);

        assertThat(response.getStatusCode().value()).isEqualTo(409);

        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(409);
        assertThat(body.error()).isEqualTo("Conflict");
        assertThat(body.message()).isEqualTo("Violação de integridade dos dados");
        assertThat(body.path()).isEqualTo("/clientes");
        assertThat(body.timestamp()).isNotNull();
    }

    @Test
    void handleNotFound_deveRetornar404() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURI()).thenReturn("/clientes/10");

        ResponseEntity<ApiError> response =
                handler.handleNotFound(new ResourceNotFoundException("Cliente não encontrado com id: 10"), req);

        assertThat(response.getStatusCode().value()).isEqualTo(404);

        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(404);
        assertThat(body.error()).isEqualTo("Not Found");
        assertThat(body.message()).isEqualTo("Cliente não encontrado com id: 10");
        assertThat(body.path()).isEqualTo("/clientes/10");
        assertThat(body.timestamp()).isNotNull();
    }

    @Test
    void handleIllegalArgument_deveRetornar400() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURI()).thenReturn("/clientes/10");

        ResponseEntity<ApiError> response =
                handler.handleIllegalArgument(new IllegalArgumentException("invalido"), req);

        assertThat(response.getStatusCode().value()).isEqualTo(400);

        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.error()).isEqualTo("Bad Request");
        assertThat(body.message()).isEqualTo("invalido");
        assertThat(body.path()).isEqualTo("/clientes/10");
        assertThat(body.timestamp()).isNotNull();
    }

    @Test
    void handleGeneric_deveRetornar500() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURI()).thenReturn("/clientes");

        ResponseEntity<ApiError> response =
                handler.handleGeneric(new RuntimeException("boom"), req);

        assertThat(response.getStatusCode().value()).isEqualTo(500);

        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(500);
        assertThat(body.error()).isEqualTo("Internal Server Error");
        assertThat(body.message()).isEqualTo("Erro interno do servidor");
        assertThat(body.path()).isEqualTo("/clientes");
        assertThat(body.timestamp()).isNotNull();
    }

    @Test
    void handleValidationErrors_deveRetornarMapaDeErrosPorCampo() throws Exception {
        Object target = new Object();
        BindingResult br = new BeanPropertyBindingResult(target, "clienteRequestDTO");
        br.addError(new FieldError("clienteRequestDTO", "name", "Nome é obrigatório"));
        br.addError(new FieldError("clienteRequestDTO", "cpf", "CPF é obrigatório"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(methodParamForValidation(), br);

        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("name", "Nome é obrigatório");
        assertThat(response.getBody()).containsEntry("cpf", "CPF é obrigatório");
    }

    private static MethodParameter methodParamForValidation() throws Exception {
        Method m = GlobalExceptionHandlerTest.class.getDeclaredMethod(
                "dummyValidationEndpoint",
                ClienteRequestDTO.class
        );
        return new MethodParameter(m, 0);
    }

    @SuppressWarnings("unused")
    private static void dummyValidationEndpoint(ClienteRequestDTO dto) {
    }
}