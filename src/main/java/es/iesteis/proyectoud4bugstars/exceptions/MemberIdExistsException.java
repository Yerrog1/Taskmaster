package es.iesteis.proyectoud4bugstars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Member ID already exists")
public class MemberIdExistsException extends Exception {
	public MemberIdExistsException() {
		super("El nombre de usuario ya está en uso");
	}
}
