package com.junior.conta_transf.DTO;

import java.math.BigDecimal;

import com.junior.conta_transf.entities.Conta;
import com.junior.conta_transf.enuns.ContaStatus;
import com.junior.conta_transf.enuns.ContaType;

public class ContaResponseDTO {
	private Long id;
    private String number;
    private ContaType type;
    private BigDecimal balance;
    private ContaStatus status;
    
    public ContaResponseDTO() {
    	
    }

    
	public ContaResponseDTO(Conta entity) {
		id = entity.getId();
		number = entity.getNumber();
		type = entity.getType();
		balance = entity.getBalance();
		status = entity.getStatus();
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public ContaType getType() {
		return type;
	}

	public void setType(ContaType type) {
		this.type = type;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public ContaStatus getStatus() {
		return status;
	}

	public void setStatus(ContaStatus status) {
		this.status = status;
	}
    
    
}

