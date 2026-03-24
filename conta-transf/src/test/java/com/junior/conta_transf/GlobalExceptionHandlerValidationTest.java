package com.junior.conta_transf;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.junior.conta_transf.exception.GlobalExceptionHandler;
import com.junior.conta_transf.exception.TestValidationController;

@WebMvcTest(controllers = TestValidationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postValidacao_quandoCampoInvalido_deveRetornar400ComFields() throws Exception {
        var body = """
            {"name": ""}
            """;

        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validação")))
                .andExpect(jsonPath("$.message", is("Campos inválidos")))
                .andExpect(jsonPath("$.path", is("/test/validation")))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.fields", hasSize(1)))
                .andExpect(jsonPath("$.fields[0].field", is("name")))
                .andExpect(jsonPath("$.fields[0].message", not(emptyString())));
    }

    @Test
    void postValidacao_quandoDoisCamposInvalidos_deveRetornar400Com2Fields() throws Exception {
        var body = """
            {"name": "", "doc": ""}
            """;

        mockMvc.perform(post("/test/validation/multi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validação")))
                .andExpect(jsonPath("$.message", is("Campos inválidos")))
                .andExpect(jsonPath("$.path", is("/test/validation/multi")))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.fields", hasSize(2)))
                .andExpect(jsonPath("$.fields[*].field", containsInAnyOrder("name", "doc")));
    }

    @RestController
    static class ValidationController {
        @PostMapping("/test/validation")
        void create(@Valid @RequestBody ValidationRequest body) {
        }
    }

    @RestController
    static class MultiValidationController {
        @PostMapping("/test/validation/multi")
        void create(@Valid @RequestBody MultiValidationRequest body) {
        }
    }

    record ValidationRequest(@NotBlank String name) {}

    record MultiValidationRequest(@NotBlank String name, @NotBlank String doc) {}
}