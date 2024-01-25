package es.iesteis.proyectoud4bugstars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Invalid credentials")
public class InvalidCredentialsException extends Exception {
	public InvalidCredentialsException() {
		super("Invalid credentials");
	}
}
