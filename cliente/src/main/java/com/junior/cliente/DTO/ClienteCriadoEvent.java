package com.junior.cliente.DTO;

import java.time.LocalDate;

public record ClienteCriadoEvent(Long id, String name, String cpf, String email, LocalDate birthDate, boolean active) {
}
