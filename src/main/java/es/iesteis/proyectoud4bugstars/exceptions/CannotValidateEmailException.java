package es.iesteis.proyectoud4bugstars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CannotValidateEmailException extends Exception {
	public CannotValidateEmailException() {
		super("Cannot validate email");
	}
}
