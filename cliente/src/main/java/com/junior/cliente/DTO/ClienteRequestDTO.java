package com.junior.cliente.DTO;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

public record ClienteRequestDTO(

		@Schema(example = "José Luiz Ribeiro Junior", description = "Nome completo do cliente") @NotBlank(message = "Nome é obrigatório") String name,
		@Schema(example = "123.456.789-00", description = "CPF do cliente") @NotBlank(message = "CPF é obrigatório") @Pattern(regexp = "([0-9]{2}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[\\/]?[0-9]{4}[-]?[0-9]{2})|([0-9]{3}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[-]?[0-9]{2})", message = "CPF (11 dígitos) ou CNPJ (14 dígitos), com ou sem pontuação") String cpf,

		@NotBlank(message = "Email é obrigatório") @Email(message = "Email inválido") String email, @Schema(

				example = "25/12/1995", pattern = "^\\d{2}/\\d{2}/\\d{4}$") @NotNull(message = "Data de nascimento é obrigatória") @Past(message = "Data de nascimento deve ser no passado") @JsonFormat(pattern = "dd/MM/yyyy") LocalDate birthDate

) {
}
