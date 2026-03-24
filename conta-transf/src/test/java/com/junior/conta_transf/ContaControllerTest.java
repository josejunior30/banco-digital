package com.junior.conta_transf;



import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.junior.conta_transf.DTO.ContaPatchRequestDTO;
import com.junior.conta_transf.DTO.ContaRequestDTO;
import com.junior.conta_transf.DTO.ContaResponseDTO;
import com.junior.conta_transf.controller.ContaController;
import com.junior.conta_transf.entities.Conta;
import com.junior.conta_transf.enuns.ContaStatus;
import com.junior.conta_transf.enuns.ContaType;
import com.junior.conta_transf.service.ContaService;

@WebMvcTest(controllers = ContaController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContaControllerTest {

 @Autowired
 private MockMvc mockMvc;

 @MockitoBean
 private ContaService service;

 private static final ContaType TYPE = ContaType.values()[0];
 private static final ContaStatus STATUS = ContaStatus.values()[0];

 @Test
 void getContas_deveRetornar200() throws Exception {
     var pageable = PageRequest.of(0, 20);
     var dto = new ContaResponseDTO(conta(1L, "0001-00000001", TYPE, bd("0.00"), STATUS, 1L));

     when(service.findAll(any())).thenReturn(new PageImpl<>(java.util.List.of(dto), pageable, 1));

     mockMvc.perform(get("/contas").accept(MediaType.APPLICATION_JSON))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.content", hasSize(1)))
             .andExpect(jsonPath("$.content[0].id", is(1)))
             .andExpect(jsonPath("$.content[0].number", is("0001-00000001")));

     verify(service).findAll(any());
 }

 @Test
 void getContaById_deveRetornar200() throws Exception {
     var dto = new ContaResponseDTO(conta(10L, "0001-00000010", TYPE, bd("1.00"), STATUS, 1L));
     when(service.findById(10L)).thenReturn(dto);

     mockMvc.perform(get("/contas/10").accept(MediaType.APPLICATION_JSON))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.id", is(10)))
             .andExpect(jsonPath("$.number", is("0001-00000010")));

     verify(service).findById(10L);
 }

 @Test
 void deleteConta_deveRetornar204() throws Exception {
     doNothing().when(service).delete(10L);

     mockMvc.perform(delete("/contas/10"))
             .andExpect(status().isNoContent());

     verify(service).delete(10L);
 }

 @Test
 void postConta_quandoOk_deveRetornar201() throws Exception {
     var saved = conta(1L, "0001-00000001", TYPE, bd("0.00"), ContaStatus.ATIVA, 99L);
     when(service.create(any())).thenReturn(saved);

     var body = """
         {"clienteId": 99, "type": "%s"}
         """.formatted(TYPE.name());

     mockMvc.perform(post("/contas")
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(body)
                     .accept(MediaType.APPLICATION_JSON))
             .andExpect(status().isCreated())
             .andExpect(jsonPath("$.id", is(1)))
             .andExpect(jsonPath("$.number", is("0001-00000001")))
             .andExpect(jsonPath("$.type", is(TYPE.name())));

     verify(service).create(any(ContaRequestDTO.class));
 }

 @Test
 void postConta_quandoServiceRetornaNull_deveRetornar400() throws Exception {
     when(service.create(any())).thenReturn(null);

     var body = """
         {"clienteId": 99, "type": "%s"}
         """.formatted(TYPE.name());

     mockMvc.perform(post("/contas")
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(body))
             .andExpect(status().isBadRequest());
 }

 @Test
 void patchConta_quandoOk_deveRetornar200() throws Exception {
     var updated = conta(2L, "0001-00000002", TYPE, bd("0.00"), STATUS, 1L);
     when(service.patchTypeStatus(eq(2L), any())).thenReturn(updated);

     var body = """
         {"status": "%s", "type": "%s"}
         """.formatted(STATUS.name(), TYPE.name());

     mockMvc.perform(patch("/contas/2")
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(body)
                     .accept(MediaType.APPLICATION_JSON))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.id", is(2)))
             .andExpect(jsonPath("$.status", is(STATUS.name())))
             .andExpect(jsonPath("$.type", is(TYPE.name())));

     verify(service).patchTypeStatus(eq(2L), any(ContaPatchRequestDTO.class));
 }

 @Test
 void patchConta_quandoServiceRetornaNull_deveRetornar400() throws Exception {
     when(service.patchTypeStatus(eq(2L), any())).thenReturn(null);

     var body = """
         {"status": "%s"}
         """.formatted(STATUS.name());

     mockMvc.perform(patch("/contas/2")
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(body))
             .andExpect(status().isBadRequest());
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