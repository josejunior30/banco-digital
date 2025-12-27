package com.junior.conta_transf.entities;

import java.math.BigDecimal;
import java.util.Objects;
import com.junior.conta_transf.enuns.ContaStatus;
import com.junior.conta_transf.enuns.ContaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "tb_conta", indexes = { @Index(name = "ix_conta_cliente_id", columnList = "cliente_id"),
		@Index(name = "ix_conta_status", columnList = "status") }, uniqueConstraints = {
				@UniqueConstraint(name = "uk_conta_number", columnNames = "number") })
public class Conta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "number", nullable = false, length = 32, unique = true)
	private String number;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 16)
	private ContaType type; 

	@Column(name = "balance", nullable = false, precision = 19, scale = 2)
	private BigDecimal balance;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 16)
	private ContaStatus status; 

	@Column(name = "cliente_id", nullable = false)
	private Long clienteId;

	@Version
	private Long version;

	public Conta() {
	}

	@PrePersist
	public void prePersist() {
		if (this.balance == null) {
			this.balance = BigDecimal.ZERO.setScale(2);
		} else if (this.balance.scale() != 2) {
			this.balance = this.balance.setScale(2);
		}

		if (this.status == null) {
			this.status = ContaStatus.ATIVA;
		}
	}

	public Long getId() {
		return id;
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

	public Long getClienteId() {
		return clienteId;
	}

	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}

	public Long getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Conta other = (Conta) obj;
		return Objects.equals(id, other.id);
	}
}
