package com.junior.cliente.DTO;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

public record ClienteRequestDTO(

		@NotBlank(message = "Nome é obrigatório") String name,

		@NotBlank(message = "CPF é obrigatório") @Pattern(regexp = "([0-9]{2}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[\\/]?[0-9]{4}[-]?[0-9]{2})|([0-9]{3}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[-]?[0-9]{2})", message = "CPF (11 dígitos) ou CNPJ (14 dígitos), com ou sem pontuação") String cpf,

		@NotBlank(message = "Email é obrigatório") @Email(message = "Email inválido") String email,

		@NotNull(message = "Data de nascimento é obrigatória") @Past(message = "Data de nascimento deve ser no passado") LocalDate birthDate

) {
}
