package com.junior.conta_transf.DTO;

import com.junior.conta_transf.enuns.ContaStatus;
import com.junior.conta_transf.enuns.ContaType;

public record ContaPatchRequestDTO(ContaStatus status,ContaType type) {

}
