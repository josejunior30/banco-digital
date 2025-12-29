package com.junior.conta_transf.DTO;

import com.junior.conta_transf.enuns.ContaType;

public record ContaRequestDTO (Long clienteId,  ContaType type) {

	
}
