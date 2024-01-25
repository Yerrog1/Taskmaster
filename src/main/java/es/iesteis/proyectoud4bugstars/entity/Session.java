package es.iesteis.proyectoud4bugstars.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class Session {
	@Id
	@Column(name = "nonce", nullable = false)
	private String nonce;

	private String userId;

	private Instant emmisionDate;

	private Instant expirationDate;

	private boolean revoked = false;

	public String getNonce() {
		return nonce;
	}

	public Session setNonce(String nonce) {
		this.nonce = nonce;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public Session setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public Instant getEmmisionDate() {
		return emmisionDate;
	}

	public Session setEmmisionDate(Instant emmisionDate) {
		this.emmisionDate = emmisionDate;
		return this;
	}

	public Instant getExpirationDate() {
		return expirationDate;
	}

	public Session setExpirationDate(Instant expirationDate) {
		this.expirationDate = expirationDate;
		return this;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public Session setRevoked(boolean revoked) {
		this.revoked = revoked;
		return this;
	}
}
